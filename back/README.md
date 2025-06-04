# SNCF Incident Reporting App - Mockup Backend

Backend for the incident reporting app. Includes two main features
- The ability to report incidents (and secondarily, to retrieve the list of reported incidents)
- The ability to call LLMs (ChatGPT, Mistral), in a secured way

## Prerequisites

### Redis
This project requires Redis to be installed and running for caching LLM responses. 

**For MacOS (using Homebrew):**
```bash
# Install Redis
brew install redis

# Start Redis service
brew services start redis
```

**For Debian/Ubuntu (using apt):**
```bash
# Update package list
sudo apt update

# Install Redis
sudo apt install redis-server -y

# Start and enable Redis service
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

You can verify Redis is running with:
```bash
redis-cli ping
```

If successful, it should reply with "PONG".

### Environment Configuration

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

### Development

Once you completed all the configuration steps once, run the server as many times as you want with
```bash
uv run uvicorn main:app --host 0.0.0.0 --port 8000 --ssl-keyfile key.pem --ssl-certfile cert.pem
```

### Production

In a production environment, you should use a reverse proxy (like Nginx) to handle SSL termination. In this case, you can run the backend without SSL:
```bash
uv run fastapi run
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
