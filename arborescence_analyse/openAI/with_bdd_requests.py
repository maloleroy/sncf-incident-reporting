def get_incidents_objets(cursor, voiture, rame):
    # Lecture des données où la rame est présente dans le champ 'rames'
    query = f"""
        SELECT DISTINCT organe 
        FROM {voiture} 
        WHERE ',' || rames || ',' LIKE ?
    """
    pattern = f'%,{rame},%'
    cursor.execute(query, (pattern,))
    resultats = cursor.fetchall()

    organes = [ligne[0] for ligne in resultats]
    print("Objets trouvés :", organes)
    return organes


def get_incidents_locn3(cursor, organes):
    query = f"SELECT * FROM voiture WHERE organe IN ({organes})"
    cursor.execute(query)
    resultats = cursor.fetchall()
    for ligne in resultats:
        print(ligne)
        locn3 += ligne[0] + ","
    return locn3

def get_incidents_loc(cursor, organes, locn3):
    query = f"SELECT * FROM localisation WHERE organe IN ({organes}) AND locn3 IN ({locn3})"
    cursor.execute(query)
    resultats = cursor.fetchall()
    for ligne in resultats:
        print(ligne)
        loc += ligne[0] + ","
    return loc

def get_incidents_locn1(cursor, organes, locn3, loc):
    query = f"SELECT * FROM localisation WHERE organe IN ({organes}) AND loc IN ({loc}) AND locn3 IN ({locn3})"
    cursor.execute(query)
    resultats = cursor.fetchall()
    for ligne in resultats:
        print(ligne)
        locn1 += ligne[0] + ","
    return locn1

def get_incidents_cat(cursor, organes, locn3, loc, locn1):
    query = f"SELECT * FROM categorie WHERE organe IN ({organes}) AND locn3 IN ({locn3}) AND loc IN ({loc}) AND locn1 IN ({locn1})"
    cursor.execute(query)
    resultats = cursor.fetchall()
    for ligne in resultats:
        print(ligne)
        cat += ligne[0] + ","
    return cat

def get_incidents_locn2(cursor, organes, locn3, loc, locn1, cat):
    query = f"SELECT * FROM localisation WHERE organe IN ({organes}) AND locn3 IN ({locn3}) AND loc IN ({loc}) AND locn1 IN ({locn1}) AND cat IN ({cat})"
    cursor.execute(query)
    resultats = cursor.fetchall()
    for ligne in resultats:
        print(ligne)
        locn2 += ligne[0] + ","
    return locn2

def get_incidents_defaillance(cursor, organes, locn3, loc, locn2, locn1, cat):
    query = f"SELECT * FROM defaillance WHERE organe IN ({organes}) AND locn3 IN ({locn3}) AND loc IN ({loc}) AND locn2 IN ({locn2}) AND locn1 IN ({locn1}) AND cat IN ({cat})"
    cursor.execute(query)
    resultats = cursor.fetchall()
    for ligne in resultats:
        print(ligne)
        defaillance += ligne[0] + ","
    return defaillance

