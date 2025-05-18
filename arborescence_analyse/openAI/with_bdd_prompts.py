def prompt_openai_objects(list_objects, message):

    messages = [
            {"role": "system", "content": "Tu es un assistant chargé d'analyser des transcriptions audio d'agents SNCF pour identifier l'objet concerné par l'incident."},
            {"role": "user", "content": f"""Voici les objets possibles :
    {list_objects}

    Transcription :
    {message}

    ⚠️ Réponds uniquement par une liste des termes exacts issus des objets fournis, séparée par des points-virgules (;).
    Si tu n'es pas sûr, écris : Je ne sais pas."""}
        ]

    return messages

def prompt_openai_locn3(list_locn3, objects, message):
    messages = [
            {"role": "system", "content": "Tu es un assistant chargé d'analyser des transcriptions audio d'agents SNCF pour identifier des informations sur l'incident."},
            {"role": "user", "content": f"""On a identifié les objets concernés possibles, identifie la précision de localisation associée. Voici les objets possibles :
    {objects}
    Voici les précisions de localisations possibles :
             {list_locn3}

    Transcription :
    {message}

    ⚠️ Réponds uniquement par une liste des termes exacts issus des objets fournis, séparée par des points-virgules (;).
    Si tu n'es pas sûr, écris : Je ne sais pas."""}
        ]

    return messages

def prompt_openai_loc(list_locs, message):
    messages = [
            {"role": "system", "content": "Tu es un assistant chargé d'analyser des transcriptions audio d'agents SNCF pour identifier la localisation concernée par l'incident."},
            {"role": "user", "content": f"""On a identifié les objets concernés possibles, identifie la localisation associée. 
    Voici les précisions de localisations possibles, associées aux objets :
             {list_locs}

    Transcription :
    {message}

    ⚠️ Réponds uniquement par un terme exact issus des objets fournis.
    Si tu n'es pas sûr, écris : Je ne sais pas."""}
        ]

    return messages

def prompt_openai_locn1(list_locn1, loc_obj, message):
    messages = [
            {"role": "system", "content": "Tu es un assistant chargé d'analyser des transcriptions audio d'agents SNCF pour identifier des informations sur l'incident."},
            {"role": "user", "content": f"""On a identifié l'objet et la localisation concernée, identifie la précision de localisation associée. L'objet et la localisation sont :
    {loc_obj}
    Voici les précisions de localisations possibles :
             {list_locn1}

    Transcription :
    {message}

    ⚠️ Réponds uniquement par un terme exact issu des objets fournis.
    Si tu n'es pas sûr, écris : Je ne sais pas."""}
        ]

    return messages

def prompt_openai_cat(list_cat, infos, message):
    messages = [
            {"role": "system", "content": "Tu es un assistant chargé d'analyser des transcriptions audio d'agents SNCF pour identifier la catégorie de l'incident."},
            {"role": "user", "content": f"""Nous savons déjà ces informations : {infos}. Voici les catégories possibles :
    {list_cat}

    Transcription :
    {message}

    ⚠️ Réponds uniquement par un terme exact issus des objets fournis.
    Si tu n'es pas sûr, écris : Je ne sais pas."""}
        ]

    return messages

def prompt_openai_locn2(list_locsn2, infos, message):
    messages = [
            {"role": "system", "content": "Tu es un assistant chargé d'analyser des transcriptions audio d'agents SNCF pour identifier la localisation concernée par l'incident."},
            {"role": "user", "content": f"""Nous savons déjà ces informations : {infos}. Voici les précisions de localisations possibles :
    {list_locsn2}

    Transcription :
    {message}

    ⚠️ Réponds uniquement par un terme exact issus des objets fournis.
    Si tu n'es pas sûr, écris : Je ne sais pas."""}
        ]

    return messages