from enum import Enum
from dataclasses import dataclass
from datetime import datetime, UTC
from pydantic import BaseModel, Field
from typing import Optional
from uuid import UUID, uuid4

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
    uuid: UUID
    timestamp: datetime
    trainType: TrainType = TrainType.DASYE
    trainCar: str = "R6H"
    seatNumber: Optional[int] = None
    transcription: str = "L'accoudoir de la place 76 est cassé."

class ConservedInformations(BaseModel):
    location: Optional[str] = None  # localisation
    precision1: Optional[str] = None  # precision_n1
    category: Optional[str] = None  # categorie
    precision2: Optional[str] = None  # precision_n2
    system: Optional[str] = None    # organe
    precision3: Optional[str] = None  # precision_n3
    subSystem: Optional[str] = None   # sous_organe
    failure: Optional[str] = None     # defaillance

class IncidentCompletionRequest(BaseModel):
    trainType: TrainType = TrainType.DASYE
    trainCar: str = "R6H"
    seatNumber: Optional[int] = None
    level: str = "category"
    selections: ConservedInformations

class IncidentCompletionResponse(BaseModel):
    options: list[str]

class InformationsPossibilities(BaseModel):
    location: Optional[list] = None
    precision1: Optional[list] = None  # precision_n1
    category: Optional[list] = None  # categorie
    precision2: Optional[list] = None  # precision_n2
    system: Optional[list] = None    # organe
    precision3: Optional[list] = None  # precision_n3
    subSystem: Optional[list] = None   # sous_organe
    failure: Optional[list] = None 


class IncidentAnalysisResponse(BaseModel):
    uuid: UUID
    timestamp: datetime
    location : str # localisation
    precision1 : str # precision_n1
    category : str # categorie
    precision2 : str # precision_n2
    system : str # organe
    precision3 : str # precision_n3
    subSystem : str # sous_organe
    failure : str # defaillance
    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "uuid": "123e4567-e89b-12d3-a456-426614174000",
                    "timestamp": "2025-05-28T12:00:00Z",
                    "location": "Place",
                    "precision1": "",
                    "category": "Siège",
                    "precision2": "",
                    "system": "Accoudoir",
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
