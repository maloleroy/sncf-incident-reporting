import sqlite3

def get_incidents_voiture():
    conn = sqlite3.connect("../arborescence_analyse/DBs/incidents.db")
    cursor = conn.cursor()
    return conn, cursor

def get_incidents_objets(cursor, voiture, rame):
    with open('sql/get_incidents_objets.sql', 'r') as file:
        sql_query = file.read()
        cursor.execute(sql_query, (rame,))
    response = cursor.fetchall()
    return response
