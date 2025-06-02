from openai import OpenAI
import json
import os
import sqlite3
from with_bdd_prompts import *
from with_bdd_requests import *

with open("../../env/keys.json", "r", encoding="utf-8") as f:
    keys = json.load(f)

key = keys["OPENAI_SNCF"]
os.environ["OPENAI_API_KEY"] = key
client = OpenAI(api_key=os.environ.get("OPENAI_API_KEY"))

def get_incidents_voiture():
    
    conn = sqlite3.connect("arborescence_analyse/incidents_schema.db")
    cursor = conn.cursor()
    return conn, cursor 



def get_incidents_objets(cursor, voiture, rame):
    # Insertion de données
    cursor.execute("""
        SELECT localisation, categorie, organe, precision_n2, precision_n3, sous_organe, defaillance
        FROM DASYE_eau_incidents
        WHERE rames LIKE '%' || ? || '%';
    """, (rame, ))
    response = cursor.fetchall()
    return response


def prompt_openai_objects(list_objects, message):

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


def ask_openai(messages, tools=None):
    response = client.chat.completions.create(
        model="gpt-4.1",  # ou "gpt-4.1" si tu l’utilises
        messages=messages,
    )
    print(response.choices[0].message.content)
    return response.choices[0].message.content

def get_response(message, rame, voiture):
    conn, cursor = get_incidents_voiture()
    with open("codes_voitures.json", "r", encoding="utf-8") as f:
        correspondance = json.load(f)
    print(correspondance[voiture])
    possibilities = get_incidents_objets(cursor, correspondance[voiture], rame)
    prompt = prompt_openai_objects(possibilities, message)
    rep = ask_openai(prompt)
    return rep

if __name__ == "__main__":
    message = "Lunette des toilettes cassée"
    rame = "R1"
    voiture = "Dasye"
    rep = get_response(message, rame, voiture)




