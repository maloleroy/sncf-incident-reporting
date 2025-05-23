from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
from sqlite3 import Connection, connect, Row
import os

import model
from incident import find_incident
from security import validate_token
from llm import get_completions
from env import load_dotenv
import incidents_db
import incidents_schema
from health import ensure_health

app = FastAPI()
load_dotenv()

@app.post("/incidents/", response_model=model.IncidentSubmittingResponse)
async def save_incident(incident: model.IncidentAnalysisResponse, db: Connection = Depends(incidents_db.get_db), _ = Depends(validate_token)):
    return incidents_db.insert_incident(incident, db)

@app.get("/incidents/", response_model=list[model.IncidentAnalysisResponse])
async def list_incidents(db: Connection = Depends(incidents_db.get_db), _ = Depends(validate_token)):
    return incidents_db.list_incidents(db)

@app.post("/mistral/", response_model=model.SimpleChatCompletion)
async def get_mistral_completion(chat_request: model.ChatRequest, _ = Depends(validate_token)):
    return get_completions("mistral-small-latest", chat_request)

@app.post("/openai/", response_model=model.SimpleChatCompletion)
async def get_openai_completion(chat_request: model.ChatRequest, _ = Depends(validate_token)):
    return get_completions("gpt-4o", chat_request)

@app.post("/objects/")
async def get_objects(trainType: str, car: str, db: Connection = Depends(incidents_schema.get_db), _ = Depends(validate_token)):
    return incidents_schema.get_incidents_objets(db, trainType, car)

@app.post("/incident-options/", response_model = model.IncidentCompletionResponse)
async def get_completion_options(trainType: str, car: str, conserved_infos: model.IncidentCompletionRequest, db: Connection = Depends(incidents_schema.get_db), _ = Depends(validate_token)):
    return incidents_schema.get_incidents_completion(db, trainType, car, conserved_infos)

@app.post("/incident-analysis/", response_model= model.IncidentAnalysisResponse)
async def get_incident_analysis(incident_info: model.IncidentAnalysisRequest, db: Connection = Depends(incidents_schema.get_db), _ = Depends(validate_token)):
    return find_incident(db, incident_info.trainType, incident_info.trainCar, incident_info.transcription)

@app.get("/health/", response_model=model.HealthCheckResponse)
async def health_check():
    ensure_health()
    return model.HealthCheckResponse()
