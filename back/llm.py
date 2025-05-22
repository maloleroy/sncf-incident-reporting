import requests
from fastapi import FastAPI, HTTPException, Depends
from model import Incident, IncidentLocation, ChatRequest
from env import require_environment_variables
from security import PASSWORD_ENV_VAR
import os


def get_completions(model: str, messages: ChatRequest):
    vars = require_environment_variables([get_env_var_name_from_model(model), PASSWORD_ENV_VAR])
    # Prepare headers and properly formatted messages
    headers = {
        "Authorization": f"Bearer {vars[get_env_var_name_from_model(model)]}",
        "Content-Type": "application/json"
    }
    data = {
        "model": model,
        "messages": [msg.dict() for msg in messages.messages],
    }

    response = requests.post(
        get_base_url_from_model(model),
        headers=headers,
        json=data
    )

    if response.status_code != 200:
        raise HTTPException(
            status_code=response.status_code,
            detail=f"LLM API error for model {model}: {response.text}"
        )

    return {
        "content": extract_llm_content(response.json())
    }

def get_base_url_from_model(model: str) -> str:
    if model == "mistral-small-latest":
        return "https://api.mistral.ai/v1/chat/completions"
    elif model == "gpt-4o":
        return "https://api.openai.com/v1/chat/completions"
    raise HTTPException(
        status_code=400,
        detail=f"Unsupported model: {model}"
    )

def get_env_var_name_from_model(model: str) -> str:
    if model == "mistral-small-latest":
        return "MISTRAL_API_KEY"
    elif model == "gpt-4o":
        return "OPENAI_API_KEY"
    raise HTTPException(
        status_code=400,
        detail=f"Unsupported model: {model}"
    )

def extract_llm_content(response_data: dict) -> str:
    if "choices" not in response_data or len(response_data["choices"]) == 0:
        raise HTTPException(
            status_code=500,
            detail="No choices returned from LLM API"
        )
    if "message" not in response_data["choices"][0]:
        raise HTTPException(
            status_code=500,
            detail="No message in the first choice returned from LLM API"
        )
    if "content" not in response_data["choices"][0]["message"]:
        raise HTTPException(
            status_code=500,
            detail="No content in the message returned from LLM API"
        )
    content = response_data["choices"][0]["message"]["content"]
    if not isinstance(content, str):
        raise HTTPException(
            status_code=500,
            detail="Invalid content type in the message returned from LLM API"
        )
    return content


def prompt_openai_objects(list_objects, message) -> list[dict[str, str]]:
    messages = [
            {"role": "system", "content": "Tu es un assistant chargé d'analyser des transcriptions audio d'agents SNCF pour identifier l'objet concerné par l'incident."},
            {"role": "user", "content": f"""Voici les objets possibles :
    {list_objects}

    Transcription :
    {message}


    ⚠️ Réponds uniquement par l'incident qui se rapproche le plus du signalement.
    Si tu n'es pas sûr, écris : Je ne sais pas."""}
        ]

    return messages

def get_response(message, possibilities):
    prompt = prompt_openai_objects(possibilities, message)
    return get_completions("gpt-4o", ChatRequest(messages=prompt))
