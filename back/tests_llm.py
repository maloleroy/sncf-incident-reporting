import sqlite3
import os
from llm import *
from model import ChatRequest
from env import load_dotenv
load_dotenv()
from incident import find_incident
from incidents_schema import get_db, INCIDENTS_SCHEMA_DB_PATH
import time
MODEL = ModelName.OPENAI

def generate_false_transcription(incident):
    prompt = f"""Tu es un agent SNCF qui souhaite déclarer un incident.
Voici les informations de l'incident :
{incident}
Tu dois générer une transcription audio plausible de cet incident, naturelle, pas formelle, pas forcément complète, utilise des synonymes. Fais ca court, imagine un agent assez préssé.
Ne réponds que par la transcription audio, sans autre texte.
"""
    response = get_completions(MODEL, ChatRequest(messages=[{"role": "user", "content": prompt}]))
    print(response)
    return response["content"]

def choose_incidents(db):
    cursor = db.cursor()
    with open('sql/get_random_incidents.sql', 'r') as file:
        sql_query = file.read()
    cursor.execute(sql_query)
    return cursor.fetchall()

def tests():
    db = sqlite3.connect(INCIDENTS_SCHEMA_DB_PATH, check_same_thread=False)
    incidents = choose_incidents(db)
    
    # Ouvre les fichiers en mode ajout (append)
    with open("successes_openai.txt", "a", encoding="utf-8") as success_file, \
         open("failures_openai.txt", "a", encoding="utf-8") as failure_file:
        
        for incident in incidents:
            time.sleep(10)
            # incident = (trainCar, date, description, trainType) ou selon ta table
            trainType = incident[-1]  # nom de la table
            trainCar = incident[0]
            transcription = generate_false_transcription(incident)
            
            print(f"Incident (trainType={trainType}): {incident}")
            response = find_incident(db, trainType, trainCar, transcription)
            
            # Hypothèse : la fonction find_incident retourne None ou False en cas d'échec
            # et un résultat (objet) en cas de succès
            if response.location == incident[1] and response.precision1 == incident[2] and response.category == incident[3] and response.precision2 == incident[4] and response.system == incident[5] and response.precision3 == incident[6] and response.subSystem == incident[7] and response.failure == incident[8]:
                # Succès : on écrit l'incident et la transcription dans successes.txt
                success_file.write("Incident:\n")
                success_file.write(str(incident) + "\n")
                success_file.write("Transcription générée:\n")
                success_file.write(transcription + "\n")
                success_file.write("Réponse:\n")
                success_file.write(str(response) + "\n")
                success_file.write("-" * 40 + "\n")
            else:
                # Échec : on écrit le vrai incident, la transcription et la réponse dans failures.txt
                failure_file.write("Incident réel:\n")
                failure_file.write(str(incident) + "\n")
                failure_file.write("Transcription donnée:\n")
                failure_file.write(transcription + "\n")
                failure_file.write("Réponse find_incident:\n")
                failure_file.write(str(response) + "\n")
                failure_file.write("-" * 40 + "\n")
            
            print(f"Réponse find_incident : {response}")

if __name__ == "__main__":
    tests()
