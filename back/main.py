from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
from sqlite3 import Connection, connect, Row
import requests
import os
from model import Incident, IncidentLocation, ChatRequest

from security import validate_token
from llm import get_completions
from env import load_dotenv
import incidents_db
import incidents_schema

app = FastAPI()
load_dotenv()

@app.post("/incidents/")
async def create_incident(incident: Incident, db: Connection = Depends(incidents_db.get_db)):
    return incidents_db.create_incident(incident, db)

@app.get("/incidents/", response_model=list[Incident])
async def read_incidents(db: Connection = Depends(incidents_db.get_db)):
    return incidents_db.read_incidents(db)

@app.post("/mistral/")
async def get_mistral_completion(chat_request: ChatRequest, _: None = Depends(validate_token)):
    return get_completions("mistral-small-latest", chat_request)

@app.post("/openai/")
async def get_openai_completion(chat_request: ChatRequest, _: None = Depends(validate_token)):
    return get_completions("gpt-4o", chat_request)

@app.get("/objects/")
async def get_objects(voiture: str, rame: str, db: Connection = Depends(incidents_schema.get_db), _: None = Depends(validate_token)):
    return incidents_schema.get_incidents_objets(db, rame, voiture)
