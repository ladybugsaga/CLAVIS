# Configuration Reference

All CLAVIS configuration is done via environment variables or a `.env` file in the project root.

---

## General Settings

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `CLAVIS_LOG_LEVEL` | Logging level (TRACE, DEBUG, INFO, WARN, ERROR) | `INFO` | No |
| `CLAVIS_CACHE_ENABLED` | Enable in-memory response caching | `true` | No |
| `CLAVIS_CACHE_TTL_MINUTES` | Cache time-to-live in minutes | `60` | No |

---

## PubMed Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `NCBI_API_KEY` | NCBI E-utilities API key | — | Recommended |
| `NCBI_EMAIL` | Email address (used for contact) | `tool@clavis.io` | Recommended |
| `PUBMED_RATE_LIMIT` | Requests per second | `10` | No |
| `PUBMED_MAX_RESULTS` | Default max results | `20` | No |

**Getting your NCBI API key:**
1. Visit [NCBI Settings](https://www.ncbi.nlm.nih.gov/account/settings/)
2. Sign in or register (free)
3. Scroll to "API Key Management" → Create

---

## Semantic Scholar Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SEMANTIC_SCHOLAR_API_KEY` | API key for higher rate limits | — | Recommended |
| `S2_RATE_LIMIT` | Requests per second | `10` (1 without key) | No |

**Getting your key:** [Semantic Scholar API](https://www.semanticscholar.org/product/api#api-key)

---
---

## ChEMBL & PubChem Configuration

These servers use default rate limits (5 req/s) and require no API keys.

---

## Servers Requiring No API Keys

These servers use free, open APIs:

| Server | Notes |
|--------|-------|
| **ChEMBL** | Manually curated bioactivity data |
| **PubChem** | Chemical property data |
| **UniProt** | Protein sequence and function |
| **ClinicalTrials.gov** | Clinical study registrations |
| **KEGG** | Biological pathways and genomes |
| **Europe PMC** | (Stub) Literature, patents, preprints |
| **arXiv** | (Stub) Preprint search |
| **Reactome** | (Stub) Pathway diagram data |

---

## Environment Variable Precedence

CLAVIS loads configuration in this order (later sources override earlier):

1. System environment variables
2. `.env` file in project root
3. Per-server environment in MCP config (e.g., Claude Desktop config)

> [!TIP]
> For production use, set variables via your system environment or MCP client config rather than `.env` files to avoid committing secrets.

---

## Unified Server Configuration

When using `clavis-unified`, all variables defined below are shared across the internal modules. You only need one server entry in your MCP config to access everything.

---

## Example `.env` File

```bash
# === Required for PubMed ===
NCBI_API_KEY=abc123yourkey
NCBI_EMAIL=researcher@university.edu

# === Recommended ===
SEMANTIC_SCHOLAR_API_KEY=your_s2_key

# === Optional ===


# === General ===
CLAVIS_LOG_LEVEL=INFO
CLAVIS_CACHE_ENABLED=true
CLAVIS_CACHE_TTL_MINUTES=60
```

---

## Next Steps

- **[Usage Guide](usage.md)** — How to connect to AI clients
- **[Troubleshooting](troubleshooting.md)** — Config-related issues
- **[Security](security.md)** — Keeping your API keys safe
