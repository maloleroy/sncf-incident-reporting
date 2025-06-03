from fastapi import HTTPException
import redis
import os

from env import load_dotenv, require_environment_variables
import incidents_db
import incidents_schema
from cache import REDIS_URL

def ensure_health():
    incidents_db.get_db()
    incidents_schema.get_db()
    require_environment_variables(["MISTRAL_API_KEY", "OPENAI_API_KEY", "PASSWORD", "REDIS_URL"])
    ensure_redis_installed_and_running()

def ensure_redis_installed_and_running():
    try:
        redis_client = redis.Redis.from_url(REDIS_URL)
        redis_client.ping()
    except redis.ConnectionError:
        raise HTTPException(status_code=500, detail="Redis is not running. Please start the Redis server.")
