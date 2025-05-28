import redis
import hashlib
import json
import os

REDIS_URL = os.getenv("REDIS_URL", "redis://localhost:6379/0")
redis_client = redis.Redis.from_url(REDIS_URL)


def make_cache_key(transcription, possibilities):
    key_data = {"transcription": transcription, "possibilities": possibilities}
    key_str = json.dumps(key_data, sort_keys=True)
    return "llm_cache:" + hashlib.sha256(key_str.encode()).hexdigest()


def get_cached_response(transcription, possibilities):
    key = make_cache_key(transcription, possibilities)
    cached = redis_client.get(key)
    if cached:
        return json.loads(cached)
    return None


def set_cached_response(transcription, possibilities, response, expire_seconds=3600):
    key = make_cache_key(transcription, possibilities)
    redis_client.set(key, json.dumps(response), ex=expire_seconds)
