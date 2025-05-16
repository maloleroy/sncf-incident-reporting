from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
from sqlite3 import Connection, connect, Row
from typing import List
from model import Incident, IncidentLocation, Message
import requests

from dotenv import load_dotenv
import os
load_dotenv()

app = FastAPI()

# Dependency to get a new database connection for each request
def get_db() -> Connection:
    conn = connect('incidents.db', check_same_thread=False)
    conn.row_factory = Row  # Enable row factory to access columns by name
    try:
        yield conn
    finally:
        conn.close()

@app.post("/incidents/")
async def create_incident(incident: Incident, db: Connection = Depends(get_db)):
    cursor = db.cursor()

    # Insert location data
    with open('sql/insert_location.sql', 'r') as file:
        sql_query = file.read()
        cursor.execute(sql_query, (incident.location.main, incident.location.precision1, incident.location.precision2, incident.location.precision3))

    # Get the ID of the inserted location
    location_id = cursor.lastrowid

    # Insert incident data
    with open('sql/insert_incident.sql', 'r') as file:
        sql_query = file.read()
        cursor.execute(sql_query, (incident.lastUpdate, location_id, incident.subSystem, incident.failure, incident.comment, incident.sealed, incident.t4Call))

    db.commit()

    return {"message": "Incident created successfully"}

@app.get("/incidents/", response_model=List[Incident])
async def read_incidents(db: Connection = Depends(get_db)):
    cursor = db.cursor()
    with open('sql/read_incidents.sql', 'r') as file:
        sql_query = file.read()
        cursor.execute(sql_query)
    rows = cursor.fetchall()

    incidents = [
        Incident(
            id=row['incident_id'],
            lastUpdate=row['lastUpdate'],
            location=IncidentLocation(
                id=row['location_id'],
                main=row['main'],
                precision1=row['precision1'],
                precision2=row['precision2'],
                precision3=row['precision3'],
            ),
            subSystem=row['subSystem'],
            failure=row['failure'],
            comment=row['comment'],
            sealed=row['sealed'],
            t4Call=row['t4Call']
        )
        for row in rows
    ]

    return incidents

@app.post("/mistral/")
async def get_mistral_completions(messages: List[Message]):  # Accept list of Message objects
    # Load and validate Mistral API key
    mistral_api_key = os.getenv("MISTRAL_API_KEY")
    if not mistral_api_key:
        raise HTTPException(status_code=500, detail="Mistral API key not configured")

    # Prepare headers and properly formatted messages
    headers = {
        "Authorization": f"Bearer {mistral_api_key}",
        "Content-Type": "application/json"
    }
    data = {
        "model": "mistral-small-latest",
        "messages": [msg.dict() for msg in messages],  # Convert Pydantic models to dicts
    }

    # Send request to Mistral's chat completions endpoint
    response = requests.post(
        "https://api.mistral.ai/v1/chat/completions",  # Correct endpoint
        headers=headers,
        json=data
    )

    # Handle errors from Mistral API
    if response.status_code != 200:
        raise HTTPException(
            status_code=response.status_code,
            detail=f"Mistral API error: {response.text}"
        )

    return response.json()

@app.post("/openai/")
async def get_openai_completion(messages: List[Message]):
    # Validate API key
    openai_api_key = os.getenv("OPENAI_API_KEY")
    if not openai_api_key:
        raise HTTPException(status_code=500, detail="OPENAI_API_KEY not found in environment variables")

    # Prepare API request
    headers = {
        "Authorization": f"Bearer {openai_api_key}",
        "Content-Type": "application/json"
    }
    
    data = {
        "model": "gpt-4o",
        "messages": [msg.dict() for msg in messages],
    }

    # Send request to OpenAI API
    response = requests.post(
        "https://api.openai.com/v1/chat/completions",
        headers=headers,
        json=data
    )

    # Handle API errors
    if response.status_code != 200:
        raise HTTPException(
            status_code=response.status_code,
            detail=f"OpenAI API error: {response.text}"
        )

    return response.json()