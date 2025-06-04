from sqlite3 import Connection, connect, Row
import os
from datetime import datetime, UTC
from uuid import UUID

import model

INCIDENTS_DB_PATH = 'incidents.db'

def initialize_db():
    conn = connect(INCIDENTS_DB_PATH, check_same_thread=False)
    cursor = conn.cursor()
    # Create tables if they don't exist
    with open('sql/create_tables.sql', 'r') as file:
        sql_query = file.read()
        cursor.executescript(sql_query)
    conn.commit()
    conn.close()

# Dependency to get a new database connection for each request
def get_db() -> Connection:
    if not os.path.exists(INCIDENTS_DB_PATH):
        initialize_db()
    conn = connect(INCIDENTS_DB_PATH, check_same_thread=False)
    conn.row_factory = Row  # Enable row factory to access columns by name
    try:
        yield conn
    finally:
        conn.close()

def insert_incident(incident: model.IncidentAnalysisResponse, db: Connection):
    cursor = db.cursor()
    
    # First check if incident with this UUID already exists
    with open('sql/check_incident_exists.sql', 'r') as file:
        check_sql = file.read()
        cursor.execute(check_sql, (str(incident.uuid),))
        result = cursor.fetchone()
        
    if result and result['count'] > 0:
        # Incident exists, update it
        with open('sql/update_incident.sql', 'r') as file:
            sql_query = file.read()
            cursor.execute(
                sql_query,
                (
                    incident.timestamp.isoformat(),
                    incident.location,
                    incident.precision1,
                    incident.category,
                    incident.precision2,
                    incident.system,
                    incident.precision3,
                    incident.subSystem,
                    incident.failure,
                    str(incident.uuid)  # WHERE clause parameter
                )
            )
        message = "Incident successfully updated"
    else:
        # Incident doesn't exist, insert it
        with open('sql/insert_incident.sql', 'r') as file:
            sql_query = file.read()
            cursor.execute(
                sql_query,
                (
                    str(incident.uuid),
                    incident.timestamp.isoformat(),
                    incident.location,
                    incident.precision1,
                    incident.category,
                    incident.precision2,
                    incident.system,
                    incident.precision3,
                    incident.subSystem,
                    incident.failure
                )
            )
        message = "Incident successfully submitted"

    db.commit()

    return model.IncidentSubmittingResponse(
        status=model.IncidentSubmittingResponseStatus.SUCCESS,
        message=message,
    )

def list_incidents(db: Connection):
    cursor = db.cursor()
    with open('sql/list_incidents.sql', 'r') as file:
        sql_query = file.read()
        cursor.execute(sql_query)
    rows = cursor.fetchall()
    
    incidents = []
    for row in rows:
        # Parse UUID and timestamp from string format
        try:
            uuid_val = UUID(row['uuid'])
            # Handle both full ISO format and simple formats
            timestamp_str = row['timestamp']
            timestamp_val = datetime.fromisoformat(timestamp_str) if timestamp_str else datetime.now(UTC)
            
            incidents.append(model.IncidentAnalysisResponse(
                uuid=uuid_val,
                timestamp=timestamp_val,
                location=row['location'],
                precision1=row['precision1'],
                category=row['category'],
                precision2=row['precision2'],
                system=row['system'],
                precision3=row['precision3'],
                subSystem=row['subSystem'],
                failure=row['failure'],
            ))
        except (ValueError, TypeError) as e:
            # Log error but continue processing other records
            import logging
            logging.error(f"Error parsing incident data: {e}, row: {row['id']}")
            continue
    
    return incidents
