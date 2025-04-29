from transformers import AutoTokenizer, AutoModelForCausalLM, BitsAndBytesConfig, pipeline
from huggingface_hub import login
import torch
from var import HF_TOKEN, MISTRAL_KEY


login(token=HF_TOKEN)

# Configuration de quantificat

# Utiliser directement le modèle depuis Hugging Face
model_name = "mistralai/Mistral-7B-v0.1"

tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForCausalLM.from_pretrained(
    model_name,
    torch_dtype=torch.bfloat16,
    device_map="auto",
    trust_remote_code=True,
)

pipe = pipeline(
    "text-generation",
    model=model,
    tokenizer=tokenizer,
    torch_dtype=torch.bfloat16,
    device_map="auto"
)

prompt = """
Voici une transcription audio d'un agent SNCF qui décrit un incident à bord d'un train. Résume cet incident sous forme de JSON structuré, en remplissant les champs suivants uniquement si l'information est présente dans le message :

{
  "Numéro de train": "...",
  "type d'incident": "...",
  "emplacement": "...",
  "description": "...",
  "niveau": "..."
}

Si un champ n’est pas mentionné clairement, mets "Je ne sais pas".

Message : "Alors je suis en train de faire le tour du train et je vois qu'il y a un problème avec la porte du wagon 3. Elle ne se ferme pas correctement et il y a un bruit étrange qui en sort. Ça se situe à l'étage. Je suis dans un TGV Ouigo."
Réponse :
"""

sequences = pipe(
    prompt,
    do_sample=True,
    max_new_tokens=100,
    temperature=0.7,
    top_k=50,
    top_p=0.95,
    num_return_sequences=1,
)

print(sequences[0]['generated_text'])