from fastapi.testclient import TestClient

from main import app
from security import get_security_header
from model import IncidentAnalysisResponse

from env import load_dotenv
load_dotenv()

client = TestClient(app)

def test_get_root():
    response = client.get("/")
    assert response.status_code == 404

def test_get_health():
    response = client.get("/health/", headers=get_security_header())
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}
    response = client.get("/health/") # we should be able to access it without auth
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}

def test_create_incident():
    incident_data = IncidentAnalysisResponse.model_config["json_schema_extra"]["examples"][0]
    response = client.post("/incidents/", json=incident_data, headers=get_security_header())
    assert response.status_code == 200
    assert response.json() == {"message": "Incident created successfully"}
    # Check if the incident was created in the database
    response = client.get("/incidents/", headers=get_security_header())
    assert response.status_code == 200
    incidents = response.json()
    assert len(incidents) > 0
    for i in incident_data:
        assert i in incidents[-1]
        assert incident_data[i] == incidents[-1][i]

def test_read_incidents():
    response = client.get("/incidents/", headers=get_security_header())
    assert response.status_code == 200
    incidents = response.json()
    assert isinstance(incidents, list)
    if incidents:
        for incident in incidents:
            assert isinstance(incident, dict)
            for key in IncidentAnalysisResponse.model_fields.keys():
                if key in incident:
                    assert isinstance(incident[key], str)
                else:
                    assert key not in incident
    else:
        assert incidents == []
