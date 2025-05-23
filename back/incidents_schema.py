import sqlite3
import model

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

def get_incidents_completion(db : sqlite3.Connection, trainType: str, car: str, conserved_infos: model.ConservedInformations) -> list:
    cursor = db.cursor()
    list_categories = ["location", "category", "system", "precision1", "precision2", "precision3", "subSystem", "failure"]
    kept_categories = []
    last_category = ""
    for cat in list_categories:
        if not cat in conserved_infos:
            break
        last_category = cat
        kept_categories.append(cat)
    with open(f'sql/get_completion_{last_category}.sql', 'r') as file:
        sql_template = file.read()
    if not trainType.isidentifier():  # Sécurité de base
        raise ValueError("Nom de table non valide.")
    
    sql_query = sql_template.replace("{train}", trainType)
    for cat in kept_categories:
        if not cat in conserved_infos:
            break
        sql_query = sql_template.replace(f"{cat}", f"{cat} = {conserved_infos[cat]}")
    cursor.execute(sql_query, (car,))
    response = cursor.fetchall()
    return response