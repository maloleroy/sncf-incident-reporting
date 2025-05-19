from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import os
from fastapi import HTTPException, Depends

security = HTTPBearer()

PASSWORD_ENV_VAR = "PASSWORD"

def validate_token(credentials: HTTPAuthorizationCredentials = Depends(security)):
    load_dotenv()
    expected_token = os.getenv(PASSWORD_ENV_VAR)
    
    if not expected_token:
        raise HTTPException(status_code=500, detail="Server configuration error")
    
    if credentials.scheme.lower() != "bearer":
        raise HTTPException(status_code=401, detail="Invalid authentication scheme")
    
    if credentials.credentials != expected_token:
        raise HTTPException(status_code=401, detail="Invalid token")


def check_password(authorization: str, password: str):
    # Verify authorization header
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(
            status_code=401,
            detail="Missing or invalid Authorization header: " + authorization
        )

    try:
        _, token = authorization.split(" ")
    except ValueError:
        raise HTTPException(
            status_code=401,
            detail="Invalid Authorization header format"
        )

    if token != password:
        raise HTTPException(
            status_code=401,
            detail="Invalid authentication credentials"
        )
