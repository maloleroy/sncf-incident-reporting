from fastapi import HTTPException

from incidents_schema import get_incidents
from llm import get_response
from model import IncidentAnalysisResponse
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def find_incident(db, train, voiture, transcription) -> IncidentAnalysisResponse:
    possibilities = get_incidents(db, train, voiture)
    # response = "('Place', '', 'Siège', '', 'Accoudoir', '', '', 'Cassé, dégradé, manquant')"
    response = get_response(transcription, possibilities)['content']
    logger.info("LLM Response: %s", response)
    if response.lower().replace(" ", "").replace(".", "") == "jenesaispas":
        raise HTTPException(status_code=404, detail="No incident found")
    try:
        response_parsed = find_categories(response)
    except IndexError as e:
        raise HTTPException(status_code=500, detail="Parsing error: %s" % e)
    response_cleared = create_incident_response(response_parsed)
    logger.info("LLM Response Parsed: %s", response_cleared)
    return response_cleared

def create_incident_response(response_parsed):
    # Create the response with all fields properly populated
    # UUID and timestamp will be auto-generated with proper formats
    return IncidentAnalysisResponse(
        location = response_parsed[0],
        precision1 = response_parsed[1],
        category = response_parsed[2],
        precision2 = response_parsed[3],
        system = response_parsed[4],
        precision3 = response_parsed[5],
        subSystem = response_parsed[6],
        failure = response_parsed[7],
    )

def find_categories(response):
    # Extraction du contenu entre parenthèses
    step1 = response.split('(', 1)[1].rsplit(')', 1)[0]
    step2 = step1.split(',')

    state = False
    result = []
    accumulate = ''

    for word in step2:
        word = word.strip()  # Supprime les espaces autour

        if word == "''":
            result.append('')
            continue

        if word.startswith("'") and word.endswith("'") and not state:
            # Cas simple : mot complet entre quotes sur une seule ligne
            result.append(word[1:-1].strip())
        elif word.startswith("'") and not state:
            # Début de mot entre quotes sur plusieurs segments
            accumulate = word[1:].strip()
            state = True
        elif word.endswith("'") and state:
            # Fin de mot multi-segment
            accumulate += ', ' + word[:-1].strip()
            result.append(accumulate)
            accumulate = ''
            state = False
        elif state:
            # Milieu de mot multi-segment
            accumulate += ', ' + word.strip()
        else:
            # Mot non quoté (rare mais possible)
            result.append(word.strip())

    # Ajout final si oubli d'un mot
    if state and accumulate:
        result.append(accumulate)

    return result
