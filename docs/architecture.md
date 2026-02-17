# Architecture

## System Overview

CLAVIS is a **monorepo** containing a shared core library and 10 independent MCP servers, each providing access to a different biomedical database.

```
┌─────────────────────────────────────────────────────────────────┐
│                        AI Assistant                             │
│              (Claude, ChatGPT, Cursor, VS Code)                 │
└──────────┬──────────────┬──────────────┬───────────────────────┘
           │ MCP/stdio    │ MCP/stdio    │ MCP/stdio
┌──────────▼───┐  ┌───────▼──────┐  ┌───▼──────────────┐
│ clavis-pubmed│  │clavis-arxiv  │  │clavis-chembl     │
│   Server     │  │   Server     │  │    Server        │
└──────────┬───┘  └───────┬──────┘  └───┬──────────────┘
           │              │              │
┌──────────▼──────────────▼──────────────▼───────────────┐
│                    clavis-core                          │
│  ┌──────────┐ ┌───────────┐ ┌────────┐ ┌──────────┐   │
│  │MCPServer │ │HttpClient │ │ Cache  │ │  Models  │   │
│  │ Protocol │ │  + Rate   │ │ (TTL)  │ │Paper,Drug│   │
│  │(JSON-RPC)│ │  Limiter  │ │        │ │Protein...│   │
│  └──────────┘ └───────────┘ └────────┘ └──────────┘   │
└────────────────────────────────────────────────────────┘
           │              │              │
┌──────────▼───┐  ┌───────▼──────┐  ┌───▼──────────────┐
│  PubMed API  │  │  arXiv API   │  │  ChEMBL API      │
│ (E-utilities)│  │  (OAI-PMH)   │  │  (REST)          │
└──────────────┘  └──────────────┘  └──────────────────┘
```

---

## Module Structure

```
CLAVIS/
├── pom.xml                      # Parent POM (aggregator)
├── clavis-core/                 # Shared library
│   └── src/main/java/io/clavis/core/
│       ├── config/              # ConfigManager, ConfigurationException
│       ├── http/                # HttpClientFactory, RateLimiter, RetryPolicy
│       ├── mcp/                 # MCPServer, MCPTool, ToolExecutionException
│       ├── cache/               # InMemoryCache<K,V>
│       ├── models/              # Paper, Drug, Protein, Pathway, etc.
│       ├── util/                # JsonUtils, ValidationUtils
│       ├── logging/             # StructuredLogger
│       └── exception/           # ClavisException, ApiException, etc.
├── clavis-pubmed/               # PubMed MCP server
├── clavis-europepmc/            # Europe PMC server (stub)
├── clavis-semanticscholar/      # Semantic Scholar server
├── clavis-arxiv/                # arXiv server (stub)
├── clavis-clinicaltrials/       # ClinicalTrials.gov server
├── clavis-chembl/               # ChEMBL server
├── clavis-pubchem/              # PubChem server
├── clavis-uniprot/              # UniProt server
├── clavis-kegg/                 # KEGG server
└── clavis-reactome/             # Reactome server (stub)
```

---

## Design Patterns

| Pattern | Where | Why |
|---------|-------|-----|
| **Singleton** | `ConfigManager` | One config instance, thread-safe |
| **Factory** | `HttpClientFactory` | Create preconfigured HTTP clients |
| **Template Method** | `MCPServer` | Common protocol, custom tools |
| **Strategy** | `RateLimiter` | Different rate limits per API |
| **Builder** | `Paper`, `Drug`, etc. | Clean construction of complex objects |
| **Token Bucket** | `RateLimiter` | Fair, bursty rate limiting |

---

## Request Lifecycle

```
1. AI Client sends JSON-RPC request via stdin
   ↓
2. MCPServer.handleMessage() parses the request
   ↓
3. Dispatches to the appropriate MCPTool
   ↓
4. Tool validates parameters
   ↓
5. Tool calls the API client (e.g., PubMedClient)
   ↓
6. Client acquires a rate limiter token
   ↓
7. Client makes HTTP request via OkHttpClient
   ↓
8. Response is parsed (XML/JSON) into domain models
   ↓
9. Models are serialized to JSON-RPC result
   ↓
10. Result written to stdout for the AI client
```

---

## Key Design Decisions

### Why Java?
- **Performance**: JVM startup is fast enough for MCP (servers are long-running)
- **Ecosystem**: Excellent XML parsing (critical for PubMed/UniProt)
- **Type Safety**: Catches bugs at compile time
- **Concurrency**: Built-in thread safety primitives

### Why Monorepo?
- **Shared code**: `clavis-core` is used by all servers
- **Atomic changes**: Update core + servers in one commit
- **Simplified CI**: One build pipeline for everything

### Why Regex for XML Parsing?
- **Speed**: 10x faster than DOM for simple extraction
- **Memory**: No full DOM tree in memory
- **Robustness**: Works even if the XML is slightly malformed

---

## Next Steps

- **[API Reference](api-reference.md)** — Tool schemas and response formats
- **[Performance](performance.md)** — Tuning and optimization
- **[Adding Servers](contributing/adding-servers.md)** — How to add a new database
