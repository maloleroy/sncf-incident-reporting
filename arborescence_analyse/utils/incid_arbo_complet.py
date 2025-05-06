import pandas as pd
import json

def excel_to_custom_nested_json(excel_path, json_path=None):
    df_raw = pd.read_excel(excel_path, header=None)
    header = df_raw.iloc[4].tolist()
    df = pd.DataFrame(df_raw.values[5:], columns=header)

    df = df.iloc[:, [21, 23, 24, 25, 28, 29, 30]]
    df.columns = ['Localisation (QR)', 'Catégorie', 'Précision à localisation N2', 'Organe', 'Précision à localisation N3', 'Sous-organe', 'Défaillance']

    df['N+1'] = df['Catégorie'].astype(str)
    df['N+2'] = df['Organe'].astype(str)

    df['N+1'] = df['N+1'].str.replace(r'( / nan)+', '', regex=True).str.replace(r'^nan / ', '', regex=True)
    df['N+2'] = df['N+2'].str.replace(r'( / nan)+', '', regex=True).str.replace(r'^nan / ', '', regex=True)

    result = {}

    for _, row in df.iterrows():
        loc = row['Localisation (QR)']
        level1 = row['N+1']
        level2 = row['N+2']

        if pd.isna(loc) or pd.isna(level1) or pd.isna(level2):
            continue

        # Créer le dictionnaire incident en excluant les NaN
        incident_dict = {
            str(row['Précision à localisation N2']): "",
            str(row['Précision à localisation N3']): "",
            str(row['Sous-organe']): "",
            str(row['Défaillance']): ""
        }
        incident_dict = {k: v for k, v in incident_dict.items() if k.lower() != 'nan'}

        if not incident_dict:
            continue

        # Accès à la liste d'incidents
        target_list = result.setdefault(loc, {}).setdefault(level1, {}).setdefault(level2, [])

        # Vérifie l'unicité avant ajout
        existing_set = {tuple(sorted(d.items())) for d in target_list}
        current_tuple = tuple(sorted(incident_dict.items()))
        if current_tuple not in existing_set:
            target_list.append(incident_dict)

    if json_path:
        with open(json_path, 'w', encoding='utf-8') as f:
            json.dump(result, f, ensure_ascii=False, indent=2)

    return result

# Exemple d'utilisation
json_data = excel_to_custom_nested_json("../Arbo SIGNALEMENT et motifs MGC.xlsx", "../incidents_arbo_comp.json")
