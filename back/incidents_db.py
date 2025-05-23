from sqlite3 import Connection, connect, Row
import os

from model import IncidentAnalysisResponse

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

def insert_incident(incident: IncidentAnalysisResponse, db: Connection):
    cursor = db.cursor()

    with open('sql/insert_incident.sql', 'r') as file:
        sql_query = file.read()
        cursor.execute(
            sql_query,
            (
                incident.location,
                incident.category,
                incident.system,
                incident.precision1,
                incident.precision2,
                incident.precision3,
                incident.subSystem,
                incident.failure
            )
        )

    db.commit()

    return {"message": "Incident created successfully"}

def list_incidents(db: Connection):
    cursor = db.cursor()
    with open('sql/list_incidents.sql', 'r') as file:
        sql_query = file.read()
        cursor.execute(sql_query)
    rows = cursor.fetchall()

    incidents = [
        IncidentAnalysisResponse(
            location=row['location'],
            category=row['category'],
            system=row['system'],
            precision1=row['precision1'],
            precision2=row['precision2'],
            precision3=row['precision3'],
            subSystem=row['subSystem'],
            failure=row['failure'],
        )
        for row in rows
    ]

    return incidents

