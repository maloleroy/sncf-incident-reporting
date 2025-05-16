from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
from sqlite3 import Connection, connect, Row
from typing import Optional
from model import Incident, IncidentLocation, ChatRequest
import requests
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials

from dotenv import load_dotenv
import os
load_dotenv()

app = FastAPI()
security = HTTPBearer()

PASSWORD_ENV_VAR = "PASSWORD"
def validate_token(credentials: HTTPAuthorizationCredentials = Depends(security)):
    load_dotenv()
    expected_token = os.getenv("PASSWORD")
    
    if not expected_token:
        raise HTTPException(status_code=500, detail="Server configuration error")
    
    if credentials.scheme.lower() != "bearer":
        raise HTTPException(status_code=401, detail="Invalid authentication scheme")
    
    if credentials.credentials != expected_token:
        raise HTTPException(status_code=401, detail="Invalid token")

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

@app.get("/incidents/", response_model=list[Incident])
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

def get_completions(model: str, messages: ChatRequest):
    vars = require_environment_variables([get_env_var_name_from_model(model), PASSWORD_ENV_VAR])
    # Prepare headers and properly formatted messages
    headers = {
        "Authorization": f"Bearer {vars[get_env_var_name_from_model(model)]}",
        "Content-Type": "application/json"
    }
    data = {
        "model": model,
        "messages": [msg.dict() for msg in messages.messages],
    }

    response = requests.post(
        get_base_url_from_model(model),
        headers=headers,
        json=data
    )

    if response.status_code != 200:
        raise HTTPException(
            status_code=response.status_code,
            detail=f"LLM API error for model {model}: {response.text}"
        )

    return {
        "content": extract_llm_content(response.json())
    }

def get_base_url_from_model(model: str) -> str:
    if model == "mistral-small-latest":
        return "https://api.mistral.ai/v1/chat/completions"
    elif model == "gpt-4o":
        return "https://api.openai.com/v1/chat/completions"
    raise HTTPException(
        status_code=400,
        detail=f"Unsupported model: {model}"
    )

def get_env_var_name_from_model(model: str) -> str:
    if model == "mistral-small-latest":
        return "MISTRAL_API_KEY"
    elif model == "gpt-4o":
        return "OPENAI_API_KEY"
    raise HTTPException(
        status_code=400,
        detail=f"Unsupported model: {model}"
    )

def extract_llm_content(response_data: dict) -> str:
    if "choices" not in response_data or len(response_data["choices"]) == 0:
        raise HTTPException(
            status_code=500,
            detail="No choices returned from LLM API"
        )
    if "message" not in response_data["choices"][0]:
        raise HTTPException(
            status_code=500,
            detail="No message in the first choice returned from LLM API"
        )
    if "content" not in response_data["choices"][0]["message"]:
        raise HTTPException(
            status_code=500,
            detail="No content in the message returned from LLM API"
        )
    content = response_data["choices"][0]["message"]["content"]
    if not isinstance(content, str):
        raise HTTPException(
            status_code=500,
            detail="Invalid content type in the message returned from LLM API"
        )
    return content

@app.post("/mistral/")
async def get_mistral_completions(chat_request: ChatRequest, _: None = Depends(validate_token)):
    return get_completions("mistral-small-latest", chat_request)

@app.post("/openai/")
async def get_openai_completion(chat_request: ChatRequest, _: None = Depends(validate_token)):
    return get_completions("gpt-4o", chat_request)

def require_environment_variables(var_names: list[str]) -> dict[str, str]:
    required_vars = {}
    for var_name in var_names:
        if not os.getenv(var_name):
            raise HTTPException(
                status_code=500,
                detail=f"{var_name} not found in environment variables"
            )
        required_vars[var_name] = os.getenv(var_name)
    return required_vars

def check_password(authorization: str, password: str):
    # Verify authorization header
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(
            status_code=401,
            detail="Missing or invalid Authorization header: " + authorization
        )

    try:
        _, token = authorization.split(" ")
    except ValueError:
        raise HTTPException(
            status_code=401,
            detail="Invalid Authorization header format"
        )

    if token != password:
        raise HTTPException(
            status_code=401,
            detail="Invalid authentication credentials"
        )
