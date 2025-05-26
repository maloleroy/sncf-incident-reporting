import sqlite3
import model
import incidents_db
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

trad = {
    "location" : "localisation",
    "precision1": "precision_n1",
    "precision2": "precision_n2",
    "precision3": "precision_n3",
    "subSystem" : "sous_organe",
    'system' : 'organe',
    "failure" : "defaillance",
    "category" : "categorie"
}

def get_db() -> sqlite3.Connection:
    conn = sqlite3.connect("../arborescence_analyse/DBs/incidents.db", check_same_thread=False)
    try:
        yield conn
    finally:
        conn.close()

def get_incidents_objets(db: sqlite3.Connection, train: str, voiture: str) -> list:
    cursor = db.cursor()
    with open('sql/get_incidents_objets.sql', 'r') as file:
        sql_query = file.read()
        cursor.execute(sql_query, (voiture,))
    response = cursor.fetchall()
    return response

def get_incidents(db: sqlite3.Connection, train: str, voiture: str) -> list:
    cursor = db.cursor()
    with open('sql/get_incidents.sql', 'r') as file:
        sql_template = file.read()
    
    # Remplace {train} par la valeur de la variable rame (attention aux injections SQL ici !)
    if not train.isidentifier():  # Sécurité de base
        raise ValueError("Nom de table non valide.")
    
    sql_query = sql_template.replace("{train}", train)
    
    cursor.execute(sql_query, (voiture,))
    response = cursor.fetchall()
    return response

def get_incidents_completion(db: sqlite3.Connection, train: str, voiture: str, conserved_infos: model.IncidentCompletionRequest) -> model.IncidentCompletionResponse:
    cursor = db.cursor()
    with open('sql/get_incidents_.sql', 'r') as file:
        sql_query = file.read()
    
    sql_query = sql_query.replace("{train}", train)
    sql_query = sql_query.replace("{level}", trad[conserved_infos.level])

    for key, value in conserved_infos.selections.dict().items():
        logger.info(f"Processing key: {trad[key]}, value: {value}")
        if value:
            sql_query = sql_query + f" AND {trad[key]} = '{value}'"
    sql_query = sql_query + ";"

    logger.info(f"Executing SQL query: {sql_query}")
    response = cursor.execute(sql_query, (voiture,)).fetchall()

    rep_tries = []
    for row in response:
        if row[0] not in rep_tries:
            rep_tries.append(row[0])
    rep_tries = set(rep_tries)  # Convert to set to remove duplicates
    logger.info(f"Unique incidents found: {rep_tries}")
    return model.IncidentCompletionResponse(options = list(rep_tries))

    
def get_incidents_completion_old(db: sqlite3.Connection, train: str, voiture: str, conserved_infos: model.IncidentCompletionRequest) -> model.IncidentCompletionResponse:
    cursor = db.cursor()
    level = conserved_infos.level
    with open(f'sql/get_incidents_{level}.sql', 'r') as file:
        sql_query = file.read()
    
    # for key, value in conserved_infos.selections.dict().items():
    #     print(f"Processing key: {key}, value: {value}")
    #     if value is not None and key in sql_query:
    #         sql_query = sql_query.replace(f"{{{key}}}", value)
    #     elif key in sql_query:
    #         sql_query = sql_query.replace(f"{{{key}}}", "NONE")
    


    sql_query = sql_query.replace("{train}", train)
    logger.info(f"Executing SQL query: {sql_query}")
    cursor.execute(sql_query, (voiture, conserved_infos.selections.dict()["location"],))
    response = cursor.fetchall()
    rep_tries = []
    for row in response:
        if row[0] not in rep_tries:
            rep_tries.append(row[0])
    rep_tries = set(rep_tries)  # Convert to set to remove duplicates
    logger.info(f"Unique incidents found: {rep_tries}")
    return model.IncidentCompletionResponse(options = list(rep_tries))