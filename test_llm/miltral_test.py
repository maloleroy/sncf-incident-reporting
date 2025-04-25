from transformers import AutoTokenizer, AutoModelForCausalLM, BitsAndBytesConfig, pipeline
from huggingface_hub import login
import torch
login(token="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")

# Configuration de quantification
bnb_config = BitsAndBytesConfig(
    load_in_4bit=True,
    bnb_4bit_quant_type="nf4",
    bnb_4bit_use_double_quant=True,
    bnb_4bit_compute_dtype=torch.bfloat16
)

# Utiliser directement le modèle depuis Hugging Face
model_name = "mistralai/Mistral-7B-v0.1"

tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForCausalLM.from_pretrained(
    model_name,
    quantization_config=bnb_config,
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

prompt = """Here is the audio recording of a french train attendant explaining an incident it is seeing. Read it and understand it to give a summary of the incident to send it to the maintenance. Record : Alors je suis en train de faire le tour du train et je vois qu'il y a un problème avec la porte du wagon 3. Elle ne se ferme pas correctement et il y a un bruit étrange qui en sort. Ca se situe à l'étage. Je suis dans un TGV Ouigo"""

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