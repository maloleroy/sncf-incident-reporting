import sqlite3
import model
import incidents_db

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

def get_incidents_completion(db: sqlite3.Connection, trainType: str, car: str, request: model.IncidentCompletionRequest) -> dict:
    import logging

    cursor = db.cursor()
    conserved_infos = request.selections

    list_categories = ["location", "precision1", "category", "precision2", "system", "precision3", "subSystem", "failure"]
    kept_categories = []
    categories_to_propose = []
    last_category = ""

    # 🧠 Lecture des catégories déjà remplies et à compléter
    for cat in list_categories:
        if cat not in conserved_infos:
            categories_to_propose.append(cat)
        else:
            last_category = cat
            kept_categories.append(cat)

    logging.debug(f"[DEBUG] Catégories conservées (déjà remplies) : {kept_categories}")
    logging.debug(f"[DEBUG] Catégories à compléter : {categories_to_propose}")
    logging.debug(f"[DEBUG] Dernière catégorie connue : {last_category}")

    if not categories_to_propose:
        logging.debug("[DEBUG] Aucune catégorie à compléter, retour vide.")
        return {}

    # 📄 Lecture du template SQL
    with open(f'sql/get_incidents_{last_category}.sql', 'r') as file:
        sql_template = file.read()

    # 🔒 Sécurité sur le nom de table
    if not trainType.isidentifier():
        raise ValueError("Nom de table non valide.")

    # 🏗 Construction dynamique de la requête SQL
    sql_query = sql_template.replace("{train}", trainType)
    for cat in kept_categories:
        sql_query = sql_query.replace(f"{{{cat}}}", f"{cat} = '{conserved_infos[cat]}'")

    logging.debug(f"[DEBUG] Requête SQL finale exécutée :\n{sql_query}")
    logging.debug(f"[DEBUG] Paramètre passé à la requête (car): {car}")

    # 🔍 Exécution SQL
    cursor.execute(sql_query, (car,))
    rows = cursor.fetchall()

    logging.debug(f"[DEBUG] Résultat brut SQL (rows): {rows}")

    # 🔁 Association colonnes <-> catégories
    response_by_category = {cat: set() for cat in categories_to_propose}
    for row in rows:
        for i, cat in enumerate(categories_to_propose):
            if i < len(row):
                response_by_category[cat].add(row[i])
                logging.debug(f"[DEBUG] Ajouté {row[i]} à la catégorie {cat}")

    # 🔄 Conversion en listes
    final_response = {cat: list(values) for cat, values in response_by_category.items()}
    logging.debug(f"[DEBUG] Résultat final structuré : {final_response}")

    return model.IncidentCompletionResponse(options=final_response[request.level])

# conserved_1.precision2 = "Boulevard de la République"
# conserved_1.system = "Boulevard"
# conserved_1.precision3 = "Boulevard de la République"
# conserved_1.subSystem = "Boulevard"
# conserved_1.failure = "Boulevard"
