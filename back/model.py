from enum import Enum
from dataclasses import dataclass
from datetime import datetime, UTC
from pydantic import BaseModel, Field
from typing import Optional


class HealthCheckResponse(BaseModel):
    status: str = "ok"
    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "status": "ok"
                }
            ]
        }
    }

# Define Pydantic model for message structure
class Message(BaseModel):
    role: str
    content: str
    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "role": "system",
                    "content": "Tu es un assistant SNCF qui reformule les signalements des chefs de bord.",
                }
            ]
        }
    }

class ChatRequest(BaseModel):
    messages: list[Message] = Field(
        default_factory=lambda: [
            Message(role="system", content="Tu es un assistant SNCF qui reformule les signalements des chefs de bord."),
            Message(role="user", content="Il y a un problème avec la porte du wagon 3.")
        ]
    )
    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "messages": [
                        {
                            "role": "system",
                            "content": "Tu es un assistant SNCF qui reformule les signalements des chefs de bord.",
                        },
                        {
                            "role": "user",
                            "content": "Il y a un problème avec la porte du wagon 3."
                        }
                    ]
                }
            ]
        }
    }

class SimpleChatCompletion(BaseModel):
    content: str

class TrainType(str, Enum):
    DASYE = "DASYE_eau_incidents",
    DUPLEX_WC_CHEM = "DUPLEX_WC_chimique_incidents",
    DUPLEX_WC_WATER = "DUPLEX_WC_EAU_incidents",
    NEODUPLEX_CHEM = "NEODUPLEX_chimique_incidents",
    OCEANE = "OCEANE_LIKE_incidents",
    OUIGO1 = "OUIGO1_incidents",
    OUIGO2 = "OUIGO2_incidents",
    PLT = "PLT_incidents",
    POS = "POS_incidents",
    P_DUPLEX = "P_DUPLEX_incidents",
    RDOM = "RDOM_incidents",
    RITA = "RITA_incidents",
    TANGO = "TANGO_incidents",
    TGV_R_TRI_FO = "TGV_R_TRI_FO_incidents",
    TGV_R_TRI = "TGV_R_TRI_incidents",
    TRAIN_2N2_3UA_LYRIA = "TRAIN_2N2_3UA_LYRIA_incidents",
    TRAIN_2N2_3UA = "TRAIN_2N2_3UA_incidents",
    TRAIN_2N2_3UFC = "TRAIN_2N2_3UFC_incidents",
    TRAIN_2N2_3UF = "TRAIN_2N2_3UF_incidents",
    TRAIN_2N2_3UH = "TRAIN_2N2_3UH_incidents"

class IncidentAnalysisRequest(BaseModel):
    trainType: TrainType = TrainType.DASYE
    trainCar: str = "R6H"
    transcription: str = "L'accoudoir de la place 76 est cassé."

class ConservedInformations(BaseModel):
    location: Optional[str] = None  # localisation
    category: Optional[str] = None  # categorie
    system: Optional[str] = None    # organe
    precision1: Optional[str] = None  # precision_n1
    precision2: Optional[str] = None  # precision_n2
    precision3: Optional[str] = None  # precision_n3
    subSystem: Optional[str] = None   # sous_organe
    failure: Optional[str] = None     # defaillance


class IncidentAnalysisResponse(BaseModel):
    location : str # localisation
    category : str # categorie
    system : str # organe
    precision1 : str # precision_n1
    precision2 : str # precision_n2
    precision3 : str # precision_n3
    subSystem : str # sous_organe
    failure : str # defaillance
    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "location": "Place",
                    "category": "Siège",
                    "system": "Accoudoir",
                    "precision1": "",
                    "precision2": "",
                    "precision3": "Fenêtre",
                    "subSystem": "",
                    "failure": "Cassé, dégradé, manquant"
                    }
            ]
        }
    }

class IncidentSubmittingResponseStatus(str, Enum):
    SUCCESS = "success"
    FAILURE = "failure"

class IncidentSubmittingResponse(BaseModel):
    status: IncidentSubmittingResponseStatus
    message: str
    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "status": "success",
                    "message": "Incident successfully submitted"
                },
                {
                    "status": "failure",
                    "message": "Incident submission failed"
                }
            ]
        }
    }
