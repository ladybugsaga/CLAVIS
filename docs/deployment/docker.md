# Docker Deployment

## Quick Start

### Build the Docker image
```dockerfile
# Dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy all server JARs
COPY clavis-pubmed/target/clavis-pubmed-*.jar /app/clavis-pubmed.jar
COPY clavis-europepmc/target/clavis-europepmc-*.jar /app/clavis-europepmc.jar
# ... (repeat for each server)

# Default: start PubMed server
ENTRYPOINT ["java", "-jar"]
CMD ["/app/clavis-pubmed.jar"]
```

### Build
```bash
# Build all modules first
mvn clean package -DskipTests

# Build Docker image
docker build -t clavis:latest .
```

### Run
```bash
docker run -it \
  -e NCBI_API_KEY=your_key \
  -e NCBI_EMAIL=your_email \
  clavis:latest /app/clavis-pubmed.jar
```

---

## Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  pubmed:
    build: .
    command: ["/app/clavis-pubmed.jar"]
    environment:
      - NCBI_API_KEY=${NCBI_API_KEY}
      - NCBI_EMAIL=${NCBI_EMAIL}
    stdin_open: true

  europepmc:
    build: .
    command: ["/app/clavis-europepmc.jar"]
    stdin_open: true

  drugbank:
    build: .
    command: ["/app/clavis-drugbank.jar"]
    environment:
      - DRUGBANK_API_KEY=${DRUGBANK_API_KEY}
    stdin_open: true
```

### Run with Docker Compose
```bash
docker-compose up
```

---

## Multi-Stage Build (Optimized)

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/clavis-pubmed/target/clavis-pubmed-*.jar /app/clavis-pubmed.jar
ENTRYPOINT ["java", "-jar", "/app/clavis-pubmed.jar"]
```

This produces a much smaller image (~200MB vs ~800MB).

---

## Connecting to Claude Desktop with Docker

```json
{
  "mcpServers": {
    "pubmed": {
      "command": "docker",
      "args": [
        "run", "-i", "--rm",
        "-e", "NCBI_API_KEY=your_key",
        "-e", "NCBI_EMAIL=your_email",
        "clavis:latest",
        "/app/clavis-pubmed.jar"
      ]
    }
  }
}
```

> [!IMPORTANT]
> The `-i` flag (interactive) is required for stdin/stdout MCP communication.

---

## Next Steps
- **[Production Deployment](production.md)** — Hardened production setup
- **[Performance](../performance.md)** — JVM tuning in containers
