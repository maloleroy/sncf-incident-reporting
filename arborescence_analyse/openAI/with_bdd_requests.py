def get_incidents_localisation(cursor, localisation):
    # Insertion de données
    cursor.execute("INSERT INTO utilisateurs (nom, age) VALUES (?, ?)", ("Alice", 30))

    # Lecture des données
    cursor.execute("SELECT * FROM utilisateurs")
    resultats = cursor.fetchall()
    for ligne in resultats:
        print(ligne)