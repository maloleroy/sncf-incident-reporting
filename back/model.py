from enum import Enum
from dataclasses import dataclass
from datetime import datetime, UTC
from pydantic import BaseModel, Field

# Enums for IncidentLocationMain
class IncidentLocationMain(str, Enum):
    ASCT_LOCAL = "ASCT_LOCAL"
    SERVICE_LOCAL = "SERVICE_LOCAL"
    NURSERY = "NURSERY"
    OFFICE_BAR = "OFFICE_BAR"
    PLACE = "PLACE"
    ACCESS_PLATFORM = "ACCESS_PLATFORM"
    INTER_CIRCULATION_PLATFORM = "INTER_CIRCULATION_PLATFORM"
    ROOM = "ROOM"
    BAR_ROOM = "BAR_ROOM"
    TOILET = "TOILET"

# Enums for IncidentLocationPrecision1
class IncidentLocationPrecision1(str, Enum):
    NONE = "NONE"
    CORRIDOR = "CORRIDOR"
    CUSTOMS = "CUSTOMS"
    RIGHT = "RIGHT"
    LEFT = "LEFT"
    OFFICE_SPACE = "OFFICE_SPACE"
    FAMILY = "FAMILY"
    KIOSK = "KIOSK"
    RIGHT_RESTORATION_SPACE = "RIGHT_RESTORATION_SPACE"
    PLATFORM = "PLATFORM"
    RECEPTION_INFO_PLACE = "RECEPTION_INFO_PLACE"
    MAIN = "MAIN"
    PSH = "PSH"
    BICYCLE = "BICYCLE"
    TRAVELERS = "TRAVELERS"

# Enums for IncidentLocationPrecision2 and IncidentLocationPrecision3
class IncidentLocationPrecision2(str, Enum):
    NONE = "NONE"

class IncidentLocationPrecision3(str, Enum):
    NONE = "NONE"

# Enums for SubSystem and IncidentFailure
class SubSystem(str, Enum):
    NONE = "NONE"

class IncidentFailure(str, Enum):
    NONE = "NONE"

class IncidentLocation(BaseModel):
    id: int
    main: IncidentLocationMain
    precision1: IncidentLocationPrecision1 = IncidentLocationPrecision1.NONE
    precision2: IncidentLocationPrecision2 = IncidentLocationPrecision2.NONE
    precision3: IncidentLocationPrecision3 = IncidentLocationPrecision3.NONE

class Incident(BaseModel):
    id: int
    lastUpdate: int = int(datetime.now(UTC).timestamp() * 1000)  # Current time in milliseconds
    location: IncidentLocation
    subSystem: SubSystem = SubSystem.NONE
    failure: IncidentFailure = IncidentFailure.NONE
    comment: str = ""
    sealed: bool = False
    t4Call: bool = False

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

class IncidentInfo(BaseModel):
    trainType: TrainType = TrainType.DASYE
    trainCar: str = "R4H"
    transcription: str = "L'accoudoir de la place 76 est cassé."

class IncidentMessage(BaseModel):
    message : str
