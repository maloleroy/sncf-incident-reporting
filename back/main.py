from fastapi import FastAPI, HTTPException, Depends
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from sqlite3 import Connection, connect, Row
import os
import json

from env import load_dotenv
load_dotenv()

import model
from incident import find_incident
from security import validate_token
from llm import get_completions
import incidents_db
import incidents_schema
from health import ensure_health
from json_encoder import encode_for_retrofit


import logging
from logging.handlers import RotatingFileHandler
from fastapi import Request

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Configure logging with rotation (10 MB per file, keep 5 backups)
log_dir = os.path.join(os.path.dirname(__file__), "logs")
os.makedirs(log_dir, exist_ok=True)
log_file = os.path.join(log_dir, "app.log")
handler = RotatingFileHandler(log_file, maxBytes=10*1024*1024, backupCount=5)
formatter = logging.Formatter(
    '%(asctime)s %(levelname)s %(name)s %(message)s', datefmt='%Y-%m-%d %H:%M:%S')
handler.setFormatter(formatter)
logging.getLogger().handlers = [handler]
logging.getLogger().setLevel(logging.INFO)
logger = logging.getLogger("app")

app = FastAPI()

@app.post("/incidents/", response_model=model.IncidentSubmittingResponse)
async def save_incident(incident: model.IncidentAnalysisResponse, db: Connection = Depends(incidents_db.get_db), _ = Depends(validate_token)):
    result = incidents_db.insert_incident(incident, db)
    return JSONResponse(content=encode_for_retrofit(result))

@app.get("/incidents/", response_model=list[model.IncidentAnalysisResponse])
async def list_incidents(db: Connection = Depends(incidents_db.get_db), _ = Depends(validate_token)):
    incidents = incidents_db.list_incidents(db)
    return JSONResponse(content=encode_for_retrofit(incidents))

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
async def get_completion_options(conserved_infos: model.IncidentCompletionRequest, db: Connection = Depends(incidents_schema.get_db), _ = Depends(validate_token)):
    logger.info(f"Requete avec: {conserved_infos}")
    print(incidents_schema.get_incidents_completion(db, conserved_infos))
    return incidents_schema.get_incidents_completion(db, conserved_infos)

@app.post("/incident-analysis/", response_model= model.IncidentAnalysisResponse)
async def get_incident_analysis(incident_info: model.IncidentAnalysisRequest, db: Connection = Depends(incidents_schema.get_db), _ = Depends(validate_token)):
    return find_incident(db, incident_info.trainType, incident_info.trainCar, incident_info.transcription)

@app.get("/health/", response_model=model.HealthCheckResponse)
async def health_check():
    ensure_health()
    return model.HealthCheckResponse()

@app.middleware("http")
async def log_requests(request: Request, call_next):
    client_host = request.client.host if request.client else "unknown"
    client_port = request.client.port if request.client else "unknown"
    log_msg = f"Request: {request.method} {request.url.path} from {client_host}:{client_port}"
    logger.info(log_msg)
    response = await call_next(request)
    return response
