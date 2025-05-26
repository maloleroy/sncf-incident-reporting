import sqlite3
import model
import incidents_db
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

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
def get_incidents_completion(db: sqlite3.Connection, request: model.IncidentCompletionRequest) -> dict:
    logger.info("demande pour : %s", request)
    cursor = db.cursor()
    conserved_infos = request.selections  # ex: {"location": "Paris", "category": "A"}

    list_categories = ["location", "precision1", "category", "precision2", "system", "precision3", "subSystem", "failure"]
    kept_categories = []
    categories_to_propose = []
    last_category = ""

    # Déterminer catégories connues et à proposer
    for cat in list_categories:
        if cat not in conserved_infos:
            categories_to_propose.append(cat)
        else:
            last_category = cat
            kept_categories.append(cat)

    if not categories_to_propose:
        logger.debug("[DEBUG] Aucune catégorie à compléter, retour vide.")
        return {}

    # Charger template SQL pour last_category
    with open(f'sql/get_incidents_{last_category}.sql', 'r') as file:
        sql_template = file.read()

    # Construire la clause WHERE avec placeholders et paramètres
    where_clauses = []
    params = [request.trainCar]
    for cat in kept_categories:
        where_clauses.append(f"{cat} LIKE ?")
        # on ajoute %valeur% pour LIKE (tu peux adapter selon besoin)
        params.append(f"%{conserved_infos[cat]}%")

    # Injecter la clause WHERE dans le template SQL
    # (Le template doit contenir un token genre {where_conditions})
    sql_query = sql_template.replace("{where_conditions}", " AND ".join(where_clauses))
    sql_query = sql_template.replace("{train}", request.trainType)

    logger.debug(f"[DEBUG] Requête SQL finale :\n{sql_query}")
    logger.debug(f"[DEBUG] Paramètres SQL : {params}")

    # Exécuter la requête avec les paramètres
    cursor.execute(sql_query, params)
    rows = cursor.fetchall()

    logger.debug(f"[DEBUG] Résultats SQL (rows) : {rows}")

    # Préparer réponse : associer colonnes retournées aux catégories à proposer
    response_by_category = {cat: set() for cat in categories_to_propose}
    for row in rows:
        for i, cat in enumerate(categories_to_propose):
            if i < len(row):
                response_by_category[cat].add(row[i])

    # Convertir sets en listes
    final_response = {cat: list(values) for cat, values in response_by_category.items()}
    logger.debug(f"[DEBUG] Résultat final structuré : {final_response}")

    return model.IncidentCompletionResponse(options=final_response[request.level])
