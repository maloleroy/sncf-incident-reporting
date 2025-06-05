from fastapi.testclient import TestClient
import uuid

from main import app
from security import get_security_header
import model
from incidents_db import check_incident_in_db
from env import load_dotenv
from incidents_schema import get_db
from incidents_db import INCIDENTS_SCHEMA_DB_PATH
import sqlite3

load_dotenv()

client = TestClient(app)

def test_get_root():
    response = client.get("/")
    assert response.status_code == 404

def test_get_health():
    response = client.get("/health/", headers=get_security_header())
    assert response.status_code == 200, f"Expected status code 200, got {response.status_code}, detail: {response.json().get('detail', 'No detail provided')}"
    assert response.json() == {"status": "ok"}
    response = client.get("/health/") # we should be able to access it without auth
    assert response.status_code == 200, f"Expected status code 200, got {response.status_code}, detail: {response.json().get('detail', 'No detail provided')}"
    assert response.json() == {"status": "ok"}

def test_create_incident():
    incident_data = model.IncidentAnalysisResponse.model_config["json_schema_extra"]["examples"][0]
    incident_data["uuid"] = str(uuid.uuid4())  # Ensure a new UUID is generated for the test
    response = client.post("/incidents/", json=incident_data, headers=get_security_header())
    assert response.status_code == 200
    assert response.json() == model.IncidentSubmittingResponse.model_config["json_schema_extra"]["examples"][0]
    # Check if the incident was created in the database
    response = client.get("/incidents/", headers=get_security_header())
    assert response.status_code == 200
    incidents = response.json()
    assert len(incidents) > 0
    for i in incident_data:
        assert i in incidents[-1]
        assert incident_data[i] == incidents[-1][i], f"Expected {incident_data[i]} but got {incidents[-1][i]} for key {i}"

def test_read_incidents():
    response = client.get("/incidents/", headers=get_security_header())
    assert response.status_code == 200
    incidents = response.json()
    assert isinstance(incidents, list)
    if incidents:
        for incident in incidents:
            assert isinstance(incident, dict)
            for key in model.IncidentAnalysisResponse.model_fields.keys():
                if key in incident:
                    assert isinstance(incident[key], str)
                else:
                    assert key not in incident
    else:
        assert incidents == []

def test_check_incident_in_db():
    db = sqlite3.connect(INCIDENTS_SCHEMA_DB_PATH, check_same_thread=False)
    incident_test = model.IncidentAnalysisResponse.model_config["json_schema_extra"]["examples"][0]
    incident_test = model.IncidentAnalysisResponse(**incident_test)
    response = check_incident_in_db(db, incident_test, "R6H", "DASYE_eau_incidents")
    assert isinstance(response, model.IncidentAnalysisResponse)
    response = response.model_dump()
    incident_test = incident_test.model_dump()
    for key in incident_test:
        if isinstance(response[key], str):
            assert str(incident_test[key]) in response[key]
        else:
            assert incident_test[key] == response[key]
    

