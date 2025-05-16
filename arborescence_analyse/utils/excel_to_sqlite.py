import pandas as pd
import sqlite3


def excel_to_sqlite(excel_path, sqlite_path):
    # Lire l'excel
    df_raw = pd.read_excel(excel_path, header=None)
    header = df_raw.iloc[4].tolist()
    df = pd.DataFrame(df_raw.values[5:], columns=header)

    # Garder les colonnes utiles
    df = df.iloc[:, [20, 21, 23, 24, 25, 28, 29, 30]]
    df.columns = [
        "Rames",
        "Localisation",
        "Catégorie",
        "Précision_N2",
        "Organe",
        "Précision_N3",
        "Sous_organe",
        "Défaillance",
    ]

    # Supprimer les lignes vides
    df = df.dropna(subset=["Rames", "Localisation", "Catégorie", "Organe"])

    # Supprimer les éventuels "nan" sous forme de texte
    df = df.replace("nan", pd.NA)
    df = df.fillna("")  # Remplacer par vide pour SQL

    # Connexion SQLite
    conn = sqlite3.connect(sqlite_path)
    cursor = conn.cursor()

    # Créer la table
    cursor.execute(
        """
        CREATE TABLE IF NOT EXISTS TGV_R_TRI_incidents (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            rames TEXT,
            localisation TEXT,
            categorie TEXT,
            organe TEXT,
            precision_n2 TEXT,
            precision_n3 TEXT,
            sous_organe TEXT,
            defaillance TEXT
        )
    """
    )

    # Insérer les données
    for _, row in df.iterrows():
        cursor.execute(
            """
            INSERT INTO TGV_R_TRI_incidents (
                rames, localisation, categorie, organe,
                precision_n2, precision_n3, sous_organe, defaillance
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """,
            (
                row["Rames"],
                row["Localisation"],
                row["Catégorie"],
                row["Organe"],
                row["Précision_N2"],
                row["Précision_N3"],
                row["Sous_organe"],
                row["Défaillance"],
            ),
        )

    conn.commit()
    conn.close()


# Exemple d'utilisation
json_data = excel_to_sqlite(
    "arborescence_analyse/Arbo.xlsx", "arborescence_analyse/DBs/incidents.db"
)
