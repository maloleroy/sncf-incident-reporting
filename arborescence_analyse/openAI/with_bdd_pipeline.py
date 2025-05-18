from openai import OpenAI
import json
import os
import sqlite3
from with_bdd_prompts import *
from with_bdd_requests import *

with open("../../env/keys.json", "r", encoding="utf-8") as f:
    keys = json.load(f)

key = keys["OPENAI_SNCF"]
os.environ["OPENAI_API_KEY"] = key
client = OpenAI(api_key=os.environ.get("OPENAI_API_KEY"))

def get_incidents_voiture(code):
    
    conn = sqlite3.connect("/Users/margauxlanglois/Documents/SNCF/s2025p2-mobile-app-incidents/arborescence_analyse/DBs/incidents.db")
    cursor = conn.cursor()
    return conn, cursor 

def get_incidents_localisation(cursor, localisation):
    # Insertion de données
    cursor.execute("INSERT INTO utilisateurs (nom, age) VALUES (?, ?)", ("Alice", 30))

    # Lecture des données
    cursor.execute("SELECT * FROM utilisateurs")
    resultats = cursor.fetchall()
    for ligne in resultats:
        print(ligne)


def ask_openai(messages, tools=None):
    response = client.chat.completions.create(
        model="gpt-4.1",  # ou "gpt-4.1" si tu l’utilises
        messages=messages,
        tools=tools,
        tool_choice="auto"
    )
    print(response.choices[0].message.content)


def appels(transcription, cursor, voiture, rame):
    with open("codes_voitures.json", "r", encoding="utf-8") as f:
        correspondance = json.load(f)
    print(correspondance[voiture])
    list_objects = get_incidents_objets(cursor, correspondance[voiture], rame)
    print("liste objets", list_objects)
    prompt_objets = prompt_openai_objects(list_objects, transcription)
    objets_response = ask_openai(prompt_objets)
    objets = objets_response.split(";")
    print("objets", objets)

    ### 2. LOCN3 (SQL)
    organes_sql = ",".join([f"'{obj.strip()}'" for obj in objets])
    print("organe_sql", organes_sql)
    locn3 = get_incidents_locn3(cursor, organes_sql)
    print("locn3", locn3)

    ### 3. LOCN3 (OpenAI)
    prompt_locn3 = prompt_openai_locn3(locn3, objets, transcription)
    
    locn3_response = ask_openai(prompt_locn3)
    print("locn3_response", locn3_response)
    locn3_list = locn3_response.split(";")
    print("locn3_list", locn3_list)
    ### 4. LOC (SQL)
    locn3_sql = ",".join([f"'{l.strip()}'" for l in locn3_list])
    print("locn3_sql", locn3_sql)
    loc = get_incidents_loc(cursor, organes_sql, locn3_sql)
    print("loc", loc)
    ### 5. LOC (OpenAI)
    prompt_loc = prompt_openai_loc(loc, transcription)
    print("prompt_loc", prompt_loc)
    loc_response = ask_openai(prompt_loc)
    print("loc_response", loc_response)

    ### 6. LOCN1 (SQL)
    loc_sql = f"'{loc_response.strip()}'"
    locn1 = get_incidents_locn1(cursor, organes_sql, locn3_sql, loc_sql)
    print("locn1", locn1)

    ### 7. LOCN1 (OpenAI)
    prompt_locn1 = prompt_openai_locn1(locn1, f"{objets} + {loc_response}", transcription)
    locn1_response = ask_openai(prompt_locn1)
    print("locn1_response", locn1_response)

    ### 8. CAT (SQL)
    locn1_sql = f"'{locn1_response.strip()}'"
    cat = get_incidents_cat(cursor, organes_sql, locn3_sql, loc_sql, locn1_sql)
    print("cat", cat)

    ### 9. CAT (OpenAI)
    prompt_cat = prompt_openai_cat(cat, f"{objets} + {loc_response} + {locn1_response}", transcription)
    cat_response = ask_openai(prompt_cat)
    print("cat_response", cat_response)

    ### 10. LOCN2 (SQL)
    cat_sql = f"'{cat_response.strip()}'"
    locn2 = get_incidents_locn2(cursor, organes_sql, locn3_sql, loc_sql, locn1_sql, cat_sql)
    print("locn2", locn2)
    ### 11. LOCN2 (OpenAI)
    prompt_locn2 = prompt_openai_locn2(locn2, f"{objets} + {loc_response} + {locn1_response} + {cat_response}", transcription)
    locn2_response = ask_openai(prompt_locn2)
    print("locn2_response", locn2_response)

    ### 12. DEFAILLANCE (SQL)
    locn2_sql = f"'{locn2_response.strip()}'"
    defaillances = get_incidents_defaillance(cursor, organes_sql, locn3_sql, loc_sql, locn2_sql, locn1_sql, cat_sql)
    print("defaillances", defaillances)
    return {
        "objets": objets,
        "locn3": locn3_list,
        "loc": loc_response,
        "locn1": locn1_response,
        "cat": cat_response,
        "locn2": locn2_response,
        "defaillances": defaillances
    }


def main(code_voiture):
    print("ok")
    conn, cursor = get_incidents_voiture(code_voiture)
    print("conn", conn)
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()
    print("tables", tables)
    for table in tables:
        print("table")
        print(table[0])

    print("ok")
    print(appels("La tablette du siège 52 dans la voiture 3 est cassée", cursor, "Dasye", "R3"))
    # Fermeture
    conn.commit()
    conn.close()

if __name__ == "__main__":
    main("Dasye")
