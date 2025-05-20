from incidents_schema import get_incidents
from llm import get_response

def find_incident(db, train, voiture, transcription):
    possibilities = get_incidents(db, train, voiture)
    response = get_response(transcription, possibilities)
    return response