from env import load_dotenv, require_environment_variables
import incidents_db
import incidents_schema

def ensure_health():
    load_dotenv()
    incidents_db.get_db()
    incidents_schema.get_db()
    require_environment_variables(["MISTRAL_API_KEY", "OPENAI_API_KEY", "PASSWORD"])
