from sqlite3 import Connection, connect, Row
import os

from model import Incident, IncidentLocation

def initialize_db():
    conn = connect('incidents.db')
    cursor = conn.cursor()
    # Create tables if they don't exist
    with open('sql/create_tables.sql', 'r') as file:
        sql_query = file.read()
        cursor.executescript(sql_query)
    conn.commit()
    conn.close()

# Dependency to get a new database connection for each request
def get_db() -> Connection:
    if not os.path.exists('incidents.db'):
        initialize_db()
    conn = connect('incidents.db', check_same_thread=False)
    conn.row_factory = Row  # Enable row factory to access columns by name
    try:
        yield conn
    finally:
        conn.close()

def create_incident(incident: Incident, db: Connection):
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

def read_incidents(db: Connection):
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

