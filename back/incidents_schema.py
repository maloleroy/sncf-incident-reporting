import sqlite3

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
