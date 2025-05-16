# SNCF Incident Reporting App - Mockup Backend

Backend for the incident reporting app. Includes two main features
- The ability to report incidents (and secondarily, to retrieve the list of reported incidents)
- The ability to call LLMs (ChatGPT, Mistral), in a secured way

## Configuration

Running this project requires having a valid `.env` file at its root. To get a grasp of the environment variables needed, look at the `.env.example` file.

To run the project with `uv`, run
```bash
uv run fastapi run
```

To run the integration tests, run
```bash
uv run pytest
```

## Tech stack

The project is managed using `uv` for its speed and reliability.
- FastAPI, as an API framework
- Pydantic, for scheme definition
- SQLite 3, as a DB
- PyTest, for integration tests (using `httpx` and `fastapi.testclient`)
