from datetime import datetime
import uuid
from fastapi.encoders import jsonable_encoder

class CustomJSONEncoder:
    """Custom JSON encoder that ensures compatibility with Kotlin/Gson"""
    
    @classmethod
    def encode_datetime(cls, dt: datetime) -> str:
        """Convert datetime to ISO 8601 format compatible with Kotlin's Instant
        Ensures only 3 digits of precision (milliseconds, not microseconds)"""
        # Convert microseconds to milliseconds (truncate to 3 decimal places)
        formatted_dt = dt.replace(microsecond=dt.microsecond // 1000 * 1000)
        return formatted_dt.isoformat().replace('+00:00', 'Z')
    
    @classmethod
    def encode_uuid(cls, id: uuid.UUID) -> str:
        """Convert UUID to string format compatible with Kotlin's UUID"""
        return str(id)

def encode_for_retrofit(obj):
    """Helper function to prepare Python objects for Retrofit/Gson consumption"""
    result = jsonable_encoder(obj)
    
    # If this is a dict or list that might contain datetime objects
    if isinstance(result, dict) and 'timestamp' in result:
        # Handle timestamp in IncidentAnalysisResponse objects
        if isinstance(result['timestamp'], str) and len(result['timestamp']) > 26:
            # Truncate microseconds to milliseconds precision
            timestamp = result['timestamp']
            if '.' in timestamp:
                base, fraction = timestamp.split('.')
                if '+' in fraction:
                    fraction, timezone = fraction.split('+')
                    fraction = fraction[:3]  # Keep only 3 digits
                    result['timestamp'] = f"{base}.{fraction}+{timezone}"
                else:
                    fraction = fraction[:3]  # Keep only 3 digits
                    result['timestamp'] = f"{base}.{fraction}"
    
    return result
