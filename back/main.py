from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
from sqlite3 import Connection, connect, Row
from typing import List
from model import Incident, IncidentLocation

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