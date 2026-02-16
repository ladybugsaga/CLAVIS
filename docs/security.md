# Security Best Practices

## API Key Management

### Never commit API keys
CLAVIS uses a `.env` file that is **gitignored** by default. Never commit this file.

```bash
# Verify .env is ignored
grep '.env' .gitignore
# Output: .env
```

### Use environment variables in production
Instead of `.env` files, use system environment variables or your MCP client's `env` config:

```json
{
  "mcpServers": {
    "pubmed": {
      "command": "java",
      "args": ["-jar", "..."],
      "env": {
        "NCBI_API_KEY": "your_key"
      }
    }
  }
}
```

### Rotate keys regularly
- NCBI API keys: No expiration, but regenerate annually
- Semantic Scholar keys: Follow their rotation policy
- DrugBank keys: Tied to institutional accounts

---

## Data Privacy

### What data does CLAVIS send?
CLAVIS sends only your **search queries** and **paper IDs** to the biomedical APIs. It does not send:
- Personal information
- Chat history
- Other tool results
- System information

### What data does CLAVIS receive?
Only the API responses: paper metadata, abstracts, author names — all public scientific information.

### What data does CLAVIS store?
- **In memory**: Cached API responses (cleared on restart)
- **On disk**: Log files in `logs/` (configurable, no API keys logged)
- **No database**: CLAVIS has no persistent database

---

## Network Security

### HTTPS only
All API communications use HTTPS. CLAVIS never makes unencrypted HTTP requests.

### No inbound connections
CLAVIS servers communicate via stdin/stdout with the AI client. They do not open any network ports or accept inbound connections.

### Dependency management
All dependencies are managed via Maven Central with version pinning:
- OkHttp 4.12.0
- Gson 2.11.0
- Jackson 2.17.0
- SLF4J 2.0.12

---

## Logging

### What is logged
- Timestamps, log levels, and thread names
- API endpoint URLs (without API keys)
- Response status codes and latencies
- Error messages and stack traces

### What is NOT logged
- API keys or secrets
- Full API response bodies
- User chat messages
- Personal information

### Log rotation
Logs rotate daily and are retained for 30 days (configurable in `logback.xml`).

---

## Vulnerability Reporting

If you discover a security vulnerability, please **do not** open a public issue. Instead:

1. Email: security@clavis.io (or your contact)
2. Include a description and steps to reproduce
3. We will respond within 48 hours

---

## Next Steps
- **[Configuration](configuration.md)** — API key setup
- **[Deployment](deployment/production.md)** — Production hardening
