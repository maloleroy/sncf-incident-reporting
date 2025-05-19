# SNCF Incident Reporting App - Mockup Backend

Backend for the incident reporting app. Includes two main features
- The ability to report incidents (and secondarily, to retrieve the list of reported incidents)
- The ability to call LLMs (ChatGPT, Mistral), in a secured way

## Configuration

Running this project requires having a valid `.env` file at its root. To get a grasp of the environment variables needed, look at the `.env.example` file.

To run the project with `uv` in a HTTPS-compatible way, start by generating (only once) a certificate with
```bash
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout key.pem -out cert.pem \
  -config openssl.conf -extensions v3_req
cp cert.pem ../appKotlin/AppV1/app/src/main/res/raw/cert.pem
```

Note that this is not required if you don't want the backend to have HTTPS support, or if you are using an externally-provided SSL certificate (typically, a DNS domain-bound certificate in the case of a production environment).

> You **have** to add this device's IP (on the frontend's network) or domain name in `openssl.conf`, else the certificate could be refused by the frontend.

## Running

Once you completed all the configuration steps once, run the server as many times as you want with
```bash
uv run uvicorn main:app --host 0.0.0.0 --port 8000 --ssl-keyfile key.pem --ssl-certfile cert.pem
```

## Testing

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
- OpenSSL for localhost HTTPS
