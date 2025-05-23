from fastapi.testclient import TestClient

from main import app
from security import get_security_header

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
    incident_data = {
        "id": 0,
        "lastUpdate": 1747384018400,
        "location": {
            "id": 0,
            "main": "ASCT_LOCAL",
            "precision1": "NONE",
            "precision2": "NONE",
            "precision3": "NONE"
        },
        "subSystem": "NONE",
        "failure": "NONE",
        "comment": "This is a test comment",
        "sealed": False,
        "t4Call": False
    }
    response = client.post("/incidents/", json=incident_data, headers=get_security_header())
    assert response.status_code == 200
    assert response.json() == {"message": "Incident created successfully"}
    # Check if the incident was created in the database
    response = client.get("/incidents/", headers=get_security_header())
    assert response.status_code == 200
    incidents = response.json()
    assert len(incidents) > 0
    assert incidents[-1]["lastUpdate"] == incident_data["lastUpdate"]
    assert incidents[-1]["location"]["main"] == incident_data["location"]["main"]
    assert incidents[-1]["location"]["precision1"] == incident_data["location"]["precision1"]
    assert incidents[-1]["location"]["precision2"] == incident_data["location"]["precision2"]
    assert incidents[-1]["location"]["precision3"] == incident_data["location"]["precision3"]
    assert incidents[-1]["subSystem"] == incident_data["subSystem"]
    assert incidents[-1]["failure"] == incident_data["failure"]
    assert incidents[-1]["comment"] == incident_data["comment"]
    assert incidents[-1]["sealed"] == incident_data["sealed"]
    assert incidents[-1]["t4Call"] == incident_data["t4Call"]

def test_read_incidents():
    response = client.get("/incidents/", headers=get_security_header())
    assert response.status_code == 200
    incidents = response.json()
    assert isinstance(incidents, list)
    if incidents:
        for incident in incidents:
            assert isinstance(incident, dict)
            assert "id" in incident
            assert "lastUpdate" in incident
            assert "location" in incident
            assert "subSystem" in incident
            assert "failure" in incident
            assert "comment" in incident
            assert "sealed" in incident
            assert "t4Call" in incident
            assert isinstance(incident["location"], dict)
            assert "id" in incident["location"]
            assert "main" in incident["location"]
            assert "precision1" in incident["location"]
            assert "precision2" in incident["location"]
            assert "precision3" in incident["location"]
            assert isinstance(incident["location"]["main"], str)
            assert isinstance(incident["location"]["precision1"], str)
            assert isinstance(incident["location"]["precision2"], str)
            assert isinstance(incident["location"]["precision3"], str)
            assert isinstance(incident["subSystem"], str)
            assert isinstance(incident["failure"], str)
            assert isinstance(incident["comment"], str)
            assert isinstance(incident["sealed"], bool)
            assert isinstance(incident["t4Call"], bool)
    else:
        assert incidents == []
