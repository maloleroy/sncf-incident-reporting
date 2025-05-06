import pandas as pd
import json

def excel_to_custom_nested_json(excel_path, json_path=None):
    # Lire tout le fichier pour accéder à l'en-tête réel
    df_raw = pd.read_excel(excel_path, header=None)

    # Récupérer l'en-tête sur la ligne 5 (index 4)
    header = df_raw.iloc[4].tolist()

    # Créer un nouveau DataFrame avec les vraies colonnes et les vraies données à partir de la ligne 6 (index 5)
    df = pd.DataFrame(df_raw.values[5:], columns=header)

    # Sélection des colonnes par leurs positions : V (col 21), X (23), Y (24), Z (25), AC (28), AD (29), AE (30)
    df = df.iloc[:, [21, 23, 24, 25, 28, 29, 30]]
    df.columns = ['Localisation (QR)', 'Catégorie', 'Précision à localisation N2', 'Organe', 'Précision à localisation N3', 'Sous-organe', 'Défaillance']

    # Création des clés composites
    df['N+1'] = df['Catégorie'].astype(str)#+ " / " + df['Précision à localisation N2'].astype(str)
    df['N+2'] = df['Organe'].astype(str) #+ " / " + df['Précision à localisation N3'].astype(str) + " / " + df['Sous-organe'].astype(str)

    # Nettoyage des clés : suppression des "nan" inutiles
    df['N+1'] = df['N+1'].str.replace(r'( / nan)+', '', regex=True).str.replace(r'^nan / ', '', regex=True)
    df['N+2'] = df['N+2'].str.replace(r'( / nan)+', '', regex=True).str.replace(r'^nan / ', '', regex=True)

    def insert_nested_dict(d, keys, value):
        for key in keys[:-1]:
            d = d.setdefault(key, {})
        d[keys[-1]] = value

    result = {}
    for _, row in df.iterrows():
        keys = [row['Localisation (QR)'], row['N+1'], row['N+2']]
        value = row['Défaillance']
        insert_nested_dict(result, keys, value)

    if json_path:
        with open(json_path, 'w', encoding='utf-8') as f:
            json.dump(result, f, ensure_ascii=False, indent=2)

    return result



# Exemple d'utilisation
json_data = excel_to_custom_nested_json("../Arbo SIGNALEMENT et motifs MGC.xlsx", "../incidents_arbo_simple.json")
