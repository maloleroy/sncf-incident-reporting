from fastapi import HTTPException
import dotenv
import os

def require_environment_variables(var_names: list[str]) -> dict[str, str]:
    required_vars = {}
    for var_name in var_names:
        if not os.getenv(var_name):
            raise HTTPException(
                status_code=500,
                detail=f"{var_name} not found in environment variables"
            )
        required_vars[var_name] = os.getenv(var_name)
    return required_vars

def load_dotenv():
    dotenv.load_dotenv()
