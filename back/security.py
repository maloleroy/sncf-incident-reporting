from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import os
from fastapi import HTTPException, Depends

security = HTTPBearer()

PASSWORD_ENV_VAR = "PASSWORD"

def get_server_password():
    password = os.getenv(PASSWORD_ENV_VAR)
    if not password:
        raise HTTPException(status_code=500, detail="Server configuration error")
    return password

def get_security_header():
    return {
        "Authorization": "Bearer " + get_server_password()
    }

def validate_token(credentials: HTTPAuthorizationCredentials = Depends(security)):    
    if credentials.scheme.lower() != "bearer":
        raise HTTPException(status_code=401, detail="Invalid authentication scheme")
    
    if credentials.credentials != get_server_password():
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
