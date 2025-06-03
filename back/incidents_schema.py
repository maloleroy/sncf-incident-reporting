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
    conn = sqlite3.connect("../arborescence_analyse/incidents_schema.db", check_same_thread=False)
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

def get_incidents_completion(db: sqlite3.Connection, conserved_infos: model.IncidentCompletionRequest) -> model.IncidentCompletionResponse:
    cursor = db.cursor()
    
    with open('sql/get_incidents_.sql', 'r') as file:
        sql_query = file.read()
    if conserved_infos.trainType not in [t.value for t in model.TrainType]:
        raise ValueError(f"Invalid argument provided : {conserved_infos.trainType}")
    sql_query = sql_query.replace("{train}", conserved_infos.trainType)
    if conserved_infos.level not in trad:
        raise ValueError(f"Invalid argument provided : {conserved_infos.level}")
    sql_query = sql_query.replace("{level}", trad[conserved_infos.level])
    values_params = [conserved_infos.trainCar]
    for key, value in conserved_infos.selections.dict().items():
        if key not in trad:
            raise ValueError(f"Invalid argument provided : {key}")
        logger.info(f"Processing key: {trad[key]}, value: {value}")
        if value:
            sql_query = sql_query + f" AND {trad[key]} = ?"
            #sql_query = sql_query + f" AND {trad[key]} = ?"
            #values_params.append(trad[key])
            values_params.append(value)
    sql_query = sql_query + ";"

    logger.info(f"Executing SQL query: {sql_query}")
    # Compter combien de paramètres sont attendus
    expected_params_count = sql_query.count("?")
    logger.info(f"Liste options query: {tuple(values_params)}")
    # S'assurer que values_params contient exactement le bon nombre
    if len(values_params) != expected_params_count:
        raise ValueError(f"Nombre de paramètres incorrect : SQL attend {expected_params_count}, mais {len(values_params)} fournis.\n"
                        f"SQL : {sql_query}\nParams : {values_params}")

    # Exécuter la requête avec les bons paramètres
    response = cursor.execute(sql_query, tuple(values_params)).fetchall()
    logger.info(f"Response: {response}")

    rep_tries = []
    for row in response:
        if row[0] not in rep_tries:
            rep_tries.append(row[0])
    rep_tries = set(rep_tries)  # Convert to set to remove duplicates
    logger.info(f"Unique incidents found: {rep_tries}")
    return model.IncidentCompletionResponse(options = list(rep_tries))

  