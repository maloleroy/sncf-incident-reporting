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

app = FastAPI()
load_dotenv()

@app.post("/incidents/")
async def create_incident(incident: model.Incident, db: Connection = Depends(incidents_db.get_db), _ = Depends(validate_token)):
    return incidents_db.create_incident(incident, db)

@app.get("/incidents/", response_model=list[model.Incident])
async def read_incidents(db: Connection = Depends(incidents_db.get_db), _ = Depends(validate_token)):
    return incidents_db.read_incidents(db)

@app.post("/mistral/", response_model=model.SimpleChatCompletion)
async def get_mistral_completion(chat_request: model.ChatRequest, _ = Depends(validate_token)):
    return get_completions("mistral-small-latest", chat_request)

@app.post("/openai/", response_model=model.SimpleChatCompletion)
async def get_openai_completion(chat_request: model.ChatRequest, _ = Depends(validate_token)):
    return get_completions("gpt-4o", chat_request)

@app.post("/objects/")
async def get_objects(trainType: str, car: str, db: Connection = Depends(incidents_schema.get_db), _ = Depends(validate_token)):
    return incidents_schema.get_incidents_objets(db, trainType, car)

@app.post("/incident-analysis/", response_model= model.IncidentAnalysisResponse)
async def get_incident_analysis(incident_info: model.IncidentAnalysisRequest, db: Connection = Depends(incidents_schema.get_db), _ = Depends(validate_token)):
    incident = find_incident(db, incident_info.trainType, incident_info.trainCar, incident_info.transcription)
    return incident
