import pandas as pd
import sqlite3


def excel_to_sqlite(excel_path, sqlite_path, table_name, train_col):
    # Lire l'excel
    df_raw = pd.read_excel(excel_path, header=None)
    header = df_raw.iloc[4].tolist()
    df = pd.DataFrame(df_raw.values[5:], columns=header)

    # Garder les colonnes utiles
    df = df.iloc[:, [train_col, 21, 22, 23, 24, 25, 28, 29, 30]]
    df.columns = [
        "Rames",
        "Localisation",
        "Précision_N1",
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
        f"""
        CREATE TABLE IF NOT EXISTS "{table_name}" (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            rames TEXT,
            localisation TEXT,
            precision_n1 TEXT,
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
            f"""
            INSERT INTO "{table_name}" (
                rames, localisation, precision_n1, categorie, organe,
                precision_n2, precision_n3, sous_organe, defaillance
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            (
                row["Rames"],
                row["Localisation"],
                row["Précision_N1"],
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


trains = [
    "RDOM_incidents",
    "RITA_incidents",
    "DUPLEX_WC_chimique_incidents",
    "DUPLEX_WC_EAU_incidents",
    "NEODUPLEX_chimique_incidents",
    "DASYE_eau_incidents",
    "P_DUPLEX_incidents",
    "TRAIN_2N2_3UF_incidents",
    "TRAIN_2N2_3UH_incidents",
    "TRAIN_2N2_3UA_incidents",
    "TRAIN_2N2_3UA_LYRIA_incidents",
    "TRAIN_2N2_3UFC_incidents",
    "OCEANE_LIKE_incidents",
    "OUIGO1_incidents",
    "OUIGO2_incidents",
    "TANGO_incidents",
    "PLT_incidents",
    "POS_incidents",
    "TGV_R_TRI_FO_incidents",
    "TGV_R_TRI_incidents",
]

for i in range(len(trains)):
    excel_to_sqlite(
        "arborescence_analyse/Arbo.xlsx",
        "arborescence_analyse/DBs/incidents2.db",
        trains[i],
        i + 1,
    )
