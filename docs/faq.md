# Frequently Asked Questions

## General

### What is CLAVIS?
CLAVIS (from Latin "key") is an ecosystem of MCP (Model Context Protocol) servers that give AI assistants like Claude, ChatGPT, and Cursor direct access to 10+ biomedical databases. Instead of the AI hallucinating facts, it can look up real papers, drugs, proteins, and clinical trials.

### What is MCP?
The [Model Context Protocol](https://modelcontextprotocol.io/) is an open standard by Anthropic that lets AI assistants use external tools. CLAVIS implements MCP to expose biomedical database searches as tools.

### Which AI clients work with CLAVIS?
- **Claude Desktop** — Native MCP support ✅
- **Cursor** — Native MCP support ✅
- **VS Code (Continue.dev, Cline)** — MCP support ✅
- **Windsurf** — MCP support ✅
- **ChatGPT** — Via MCP bridge

### Is CLAVIS free?
Yes. CLAVIS itself is open source under the MIT License. Most biomedical APIs it connects to are also free (PubMed, Europe PMC, arXiv, PubChem, UniProt, etc.). Some APIs like DrugBank require academic registration.

### Which databases are supported?
| Database | Type | Status |
|----------|------|--------|
| PubMed | Literature (36M+ papers) | ✅ Ready |
| Europe PMC | Literature (open access) | 🔧 Stub |
| Semantic Scholar | Literature (AI-powered) | 🔧 Stub |
| arXiv | Preprints | 🔧 Stub |
| ClinicalTrials.gov | Clinical trials | 🔧 Stub |
| DrugBank | Drug data | 🔧 Stub |
| PubChem | Chemical compounds | 🔧 Stub |
| UniProt | Proteins | 🔧 Stub |
| KEGG | Pathways | 🔧 Stub |
| Reactome | Pathways | 🔧 Stub |

---

## Setup

### Do I need all API keys?
No. Only configure keys for the servers you want to use. PubMed requires an NCBI API key (free). Europe PMC, arXiv, ClinicalTrials.gov, PubChem, UniProt, KEGG, and Reactome need no keys at all.

### Can I run multiple servers at once?
Yes. Each server is an independent process. Your AI client can connect to multiple servers simultaneously and use tools from all of them.

### Does CLAVIS need internet access?
Yes. CLAVIS makes HTTP requests to the biomedical database APIs on your behalf. It does not cache the entire database locally.

### What Java version do I need?
Java 17 or later. Java 21 (LTS) is recommended.

---

## Usage

### How do I know CLAVIS is connected?
In Claude Desktop, look for the 🔧 tools icon in the chat input area. Click it to see available tools like `search_pubmed`.

### Can I use natural language queries?
Yes! The AI translates your natural language into PubMed query syntax automatically. Just ask normally:
> "Find recent papers about CRISPR for sickle cell disease"

### How many results can I get?
Up to 100 per search (configurable). For larger result sets, refine your query or paginate.

### Are results cached?
Yes, by default. Results are cached in memory for 60 minutes to reduce API calls. Configure via `CLAVIS_CACHE_ENABLED` and `CLAVIS_CACHE_TTL_MINUTES`.

### Can CLAVIS read full paper text?
No. CLAVIS returns metadata (title, abstract, authors, DOI, etc.). Full-text access depends on the journal's open access status. For open-access papers, use the DOI or URL to read the full text.

---

## Technical

### Why Java?
- Excellent XML parsing (critical for PubMed)
- Strong type safety for complex API responses
- Built-in concurrency for rate limiting
- Mature ecosystem with production-grade libraries

### How does rate limiting work?
CLAVIS uses a **token bucket** algorithm. Each server has a configurable rate limit (e.g., 10 requests/second for PubMed). If you exceed the limit, requests queue automatically — you never get rate-limit errors from the AI client.

### Does CLAVIS store any data?
Only in-memory cache (cleared on restart). No data is persisted to disk. No query history is stored. API keys are never logged.

### Can I use CLAVIS in production?
Yes. CLAVIS is designed for production use with proper error handling, retry logic, rate limiting, and structured logging.

---

## Contributing

### How do I add a new database?
See [Adding New Servers](contributing/adding-servers.md). The pattern is:
1. Create a module from the template
2. Implement the API client
3. Define MCP tools
4. Add tests

### Can I use CLAVIS for non-biomedical databases?
Absolutely. The `clavis-core` module is database-agnostic. You can create MCP servers for any REST/XML API.

---

## Next Steps
- **[Quick Start](quickstart.md)** — Get running in 5 minutes
- **[Troubleshooting](troubleshooting.md)** — Fix common issues
- **[Architecture](architecture.md)** — Understand the system design
