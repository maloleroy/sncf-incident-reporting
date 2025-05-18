import pygame
import sys
import json
import os
import sqlite3
from openai import OpenAI

# --- OpenAI setup ---
with open("../../env/keys.json", "r", encoding="utf-8") as f:
    keys = json.load(f)

key = keys["OPENAI_SNCF"]
os.environ["OPENAI_API_KEY"] = key
client = OpenAI(api_key=os.environ.get("OPENAI_API_KEY"))

# --- BDD et GPT ---
def get_incidents_voiture():
    conn = sqlite3.connect("/Users/margauxlanglois/Documents/SNCF/s2025p2-mobile-app-incidents/arborescence_analyse/DBs/incidents.db")
    cursor = conn.cursor()
    return conn, cursor 

def get_incidents_objets(cursor, voiture, rame):
    cursor.execute("""
        SELECT localisation, categorie, organe, precision_n2, precision_n3, sous_organe, defaillance
        FROM DASYE_eau_incidents
        WHERE rames LIKE '%' || ? || '%';
    """, (rame, ))
    response = cursor.fetchall()
    return response

def prompt_openai_objects(list_objects, message):
    messages = [
        {"role": "system", "content": "Tu es un assistant chargé d'analyser des transcriptions audio d'agents SNCF pour identifier l'objet concerné par l'incident."},
        {"role": "user", "content": f"""Voici les objets possibles :
{list_objects}

Transcription :
{message}

⚠️ Réponds uniquement par l'incident qui se rapproche le plus du signalement.
Si tu n'es pas sûr, écris : Je ne sais pas."""}
    ]
    return messages

def ask_openai(messages):
    response = client.chat.completions.create(
        model="gpt-4.1",
        messages=messages,
    )
    return response.choices[0].message.content

def get_response(message, rame, voiture):
    conn, cursor = get_incidents_voiture()
    with open("codes_voitures.json", "r", encoding="utf-8") as f:
        correspondance = json.load(f)
    voiture_code = correspondance.get(voiture, voiture)  # fallback si pas trouvé
    possibilities = get_incidents_objets(cursor, voiture_code, rame)
    prompt = prompt_openai_objects(possibilities, message)
    rep = ask_openai(prompt)
    return rep

# --- Pygame GUI ---
pygame.init()
WIDTH, HEIGHT = 800, 600
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Assistant SNCF")

font = pygame.font.Font(None, 32)
clock = pygame.time.Clock()

# Text inputs
inputs = {
    "voiture": {"rect": pygame.Rect(150, 50, 500, 32), "text": "", "active": False},
    "rame": {"rect": pygame.Rect(150, 100, 500, 32), "text": "", "active": False},
    "message": {"rect": pygame.Rect(150, 150, 500, 32), "text": "", "active": False},
}

response_text = ""

def draw():
    screen.fill((255, 255, 255))
    title = font.render("Assistant Incidents SNCF", True, (0, 0, 0))
    screen.blit(title, (WIDTH//2 - title.get_width()//2, 10))

    y = 50
    for label, data in inputs.items():
        label_surf = font.render(label.capitalize() + ":", True, (0, 0, 0))
        screen.blit(label_surf, (50, y))
        pygame.draw.rect(screen, (200, 200, 200), data["rect"], 2)
        text_surface = font.render(data["text"], True, (0, 0, 0))
        screen.blit(text_surface, (data["rect"].x+5, data["rect"].y+5))
        y += 50

    # Bouton Envoyer
    pygame.draw.rect(screen, (100, 200, 100), (300, 210, 200, 40))
    btn_text = font.render("Envoyer", True, (255, 255, 255))
    screen.blit(btn_text, (370, 220))

    # Affichage réponse
    y = 280
    for line in response_text.splitlines():
        line_surf = font.render(line, True, (0, 0, 0))
        screen.blit(line_surf, (50, y))
        y += 30

    pygame.display.flip()

while True:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            sys.exit()

        if event.type == pygame.MOUSEBUTTONDOWN:
            for data in inputs.values():
                data["active"] = False
            for key, data in inputs.items():
                if data["rect"].collidepoint(event.pos):
                    data["active"] = True

            # Clique sur bouton
            if pygame.Rect(300, 210, 200, 40).collidepoint(event.pos):
                voiture = inputs["voiture"]["text"]
                rame = inputs["rame"]["text"]
                message = inputs["message"]["text"]
                try:
                    response_text = get_response(message, rame, voiture)
                except Exception as e:
                    response_text = f"Erreur : {e}"

        if event.type == pygame.KEYDOWN:
            for key, data in inputs.items():
                if data["active"]:
                    if event.key == pygame.K_BACKSPACE:
                        data["text"] = data["text"][:-1]
                    else:
                        data["text"] += event.unicode

    draw()
    clock.tick(30)
