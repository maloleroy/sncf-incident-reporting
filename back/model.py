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

# Data class for IncidentLocation
@dataclass
class IncidentLocation(BaseModel):
    main: IncidentLocationMain
    precision1: IncidentLocationPrecision1 = IncidentLocationPrecision1.NONE
    precision2: IncidentLocationPrecision2 = IncidentLocationPrecision2.NONE
    precision3: IncidentLocationPrecision3 = IncidentLocationPrecision3.NONE
    id: int = 0

# Data class for Incident
@dataclass
class Incident(BaseModel):
    lastUpdate: int = int(datetime.now(UTC).timestamp() * 1000)  # Current time in milliseconds
    location: IncidentLocation
    subSystem: SubSystem = SubSystem.NONE
    failure: IncidentFailure = IncidentFailure.NONE
    comment: str = ""
    sealed: bool = False
    t4Call: bool = False
