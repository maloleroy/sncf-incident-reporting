from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import os
from fastapi import HTTPException, Depends
import logging

security = HTTPBearer()

PASSWORD_ENV_VAR = "PASSWORD"

logger = logging.getLogger("app")

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
        logger.warning(f"Auth failed: invalid scheme from client. Credentials: {credentials}")
        raise HTTPException(status_code=401, detail="Invalid authentication scheme")
    
    if credentials.credentials != get_server_password():
        logger.warning(f"Auth failed: bad password from client.")
        raise HTTPException(status_code=401, detail="Invalid token")


def check_password(authorization: str, password: str):
    # Verify authorization header
    if not authorization or not authorization.startswith("Bearer "):
        logger.warning(f"Auth failed: missing or invalid Authorization header: {authorization}")
        raise HTTPException(
            status_code=401,
            detail="Missing or invalid Authorization header: " + str(authorization)
        )

    try:
        _, token = authorization.split(" ")
    except ValueError:
        logger.warning(f"Auth failed: invalid Authorization header format: {authorization}")
        raise HTTPException(
            status_code=401,
            detail="Invalid Authorization header format"
        )

    if token != password:
        logger.warning(f"Auth failed: bad password in check_password.")
        raise HTTPException(
            status_code=401,
            detail="Invalid authentication credentials"
        )
