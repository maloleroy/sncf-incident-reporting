from datetime import datetime
import uuid
from fastapi.encoders import jsonable_encoder

class CustomJSONEncoder:
    """Custom JSON encoder that ensures compatibility with Kotlin/Gson"""
    
    @classmethod
    def encode_datetime(cls, dt: datetime) -> str:
        """Convert datetime to ISO 8601 format compatible with Kotlin's Instant"""
        return dt.isoformat()
    
    @classmethod
    def encode_uuid(cls, id: uuid.UUID) -> str:
        """Convert UUID to string format compatible with Kotlin's UUID"""
        return str(id)

def encode_for_retrofit(obj):
    """Helper function to prepare Python objects for Retrofit/Gson consumption"""
    result = jsonable_encoder(obj)
    return result
