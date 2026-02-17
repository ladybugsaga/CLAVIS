# Usage Guide — Connecting CLAVIS to AI Assistants

CLAVIS MCP servers communicate over **stdin/stdout using the Model Context Protocol (MCP)**. This guide shows you how to connect them to every major AI client.

---

## How MCP Works

```
┌─────────────┐    stdin     ┌─────────────────┐    HTTPS    ┌──────────┐
│  AI Client   │───────────▶│  CLAVIS Server   │───────────▶│ PubMed   │
│ (Claude etc) │◀───────────│  (Java process)  │◀───────────│ API      │
└─────────────┘   stdout    └─────────────────┘            └──────────┘
```

1. The AI client starts CLAVIS as a subprocess
2. It sends JSON-RPC requests over stdin
3. CLAVIS calls the biomedical API and returns results over stdout
4. The AI uses the results to answer your question

---

## Connecting to Claude Desktop

### Step 1: Locate your Claude config
| OS | Path |
|----|------|
| macOS | `~/Library/Application Support/Claude/claude_desktop_config.json` |
| Windows | `%APPDATA%\Claude\claude_desktop_config.json` |
| Linux | `~/.config/claude/claude_desktop_config.json` |

### Step 2: Add CLAVIS servers

Edit (or create) the config file:

```json
{
  "mcpServers": {
    "clavis-pubmed": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/CLAVIS/clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "NCBI_API_KEY": "your_api_key_here",
        "NCBI_EMAIL": "your_email@example.com"
      }
    },
    "clavis-europepmc": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/CLAVIS/clavis-europepmc/target/clavis-europepmc-1.0.0-SNAPSHOT.jar"
      ]
    },
    "clavis-semanticscholar": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/CLAVIS/clavis-semanticscholar/target/clavis-semanticscholar-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "SEMANTIC_SCHOLAR_API_KEY": "your_key_here"
      }
    }
  }
}
```

### Step 3: Restart Claude Desktop

Quit and reopen Claude Desktop. You should see the 🔧 tools icon appear, indicating CLAVIS servers are connected.

### Step 4: Ask Claude

Try prompts like:
- *"Search PubMed for recent CRISPR gene therapy papers"*
- *"Find the paper with PMID 33116279 and summarize it"*
- *"What papers are related to this CRISPR study?"*

---

## Connecting to VS Code (Continue.dev / Cline / Copilot)

### Continue.dev

Add to your `~/.continue/config.json`:

```json
{
  "experimental": {
    "modelContextProtocolServers": [
      {
        "transport": {
          "type": "stdio",
          "command": "java",
          "args": [
            "-jar",
            "/path/to/CLAVIS/clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar"
          ],
          "env": {
            "NCBI_API_KEY": "your_key",
            "NCBI_EMAIL": "your_email"
          }
        }
      }
    ]
  }
}
```

### Cline (VS Code Extension)

1. Open Cline settings in VS Code
2. Navigate to **MCP Servers** section
3. Click **Add Server** → **Local (stdio)**
4. Configure:
   - **Name**: `clavis-pubmed`
   - **Command**: `java`
   - **Args**: `-jar /path/to/CLAVIS/clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar`
5. Add environment variables for API keys

---

## Connecting to ChatGPT (via MCP Bridge)

ChatGPT doesn't natively support MCP yet. Use an MCP-to-OpenAI bridge:

```bash
# Install an MCP bridge (example using mcp-proxy)
npm install -g @anthropic/mcp-proxy

# Start the bridge
mcp-proxy --server "java -jar /path/to/clavis-pubmed-1.0.0-SNAPSHOT.jar" \
          --name "clavis-pubmed" \
          --port 8080
```

Then configure ChatGPT to use the bridge as a custom tool endpoint.

---

## Connecting to Cursor

Add to your `.cursor/mcp.json` in your project root:

```json
{
  "mcpServers": {
    "clavis-pubmed": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/CLAVIS/clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "NCBI_API_KEY": "your_key",
        "NCBI_EMAIL": "your_email"
      }
    }
  }
}
```

Restart Cursor to activate.

---

## Connecting to Windsurf

Add to `~/.windsurf/mcp_config.json`:

```json
{
  "mcpServers": {
    "clavis-pubmed": {
      "command": "java",
      "args": ["-jar", "/path/to/clavis-pubmed-1.0.0-SNAPSHOT.jar"],
      "env": {
        "NCBI_API_KEY": "your_key",
        "NCBI_EMAIL": "your_email"
      }
    }
  }
}
```

---

## Running Manually (Testing)

You can test any CLAVIS server directly from the terminal:

```bash
# Start the PubMed server
java -jar clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar
```

Then send JSON-RPC messages via stdin:

```json
{"jsonrpc":"2.0","id":1,"method":"initialize","params":{}}
```

```json
{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}
```

```json
{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"search_pubmed","arguments":{"query":"CRISPR gene therapy","maxResults":5}}}
```

---

## Running Multiple Servers

You can run multiple CLAVIS servers simultaneously. Each server is a separate process:

```json
{
  "mcpServers": {
    "clavis-pubmed": {
      "command": "java",
      "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-pubmed-1.0.0-SNAPSHOT.jar"],
      "env": {"NCBI_API_KEY": "key", "NCBI_EMAIL": "email"}
    },
    "clavis-semanticscholar": {
      "command": "java",
      "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-semanticscholar-1.0.0-SNAPSHOT.jar"]
    },
    "clavis-chembl": {
      "command": "java",
      "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-chembl-1.0.0-SNAPSHOT.jar"]
    },
    "clavis-pubchem": {
      "command": "java",
      "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-pubchem-1.0.0-SNAPSHOT.jar"]
    },
    "clavis-clinicaltrials": {
      "command": "java",
      "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-clinicaltrials-1.0.0-SNAPSHOT.jar"]
    },
    "clavis-uniprot": {
      "command": "java",
      "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-uniprot-1.0.0-SNAPSHOT.jar"]
    },
    "clavis-kegg": {
      "command": "java",
      "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-kegg-1.0.0-SNAPSHOT.jar"]
    }
  }
}
```

The AI client will discover tools from all connected servers and can use them together in a single conversation.

---

## Available Tools Per Server

| Server | Tools | Status |
|--------|-------|--------|
| **PubMed** | `search_pubmed`, `get_pubmed_paper`, `get_related_papers`, `track_citations`, `search_by_author`, `check_retractions`, `batch_retrieve`, `get_related_database_links` | ✅ Ready |
| **Europe PMC** | *(not yet implemented)* | 🔧 Stub |
| **Semantic Scholar** | `s2_search`, `s2_get_paper`, `s2_get_citations`, `s2_get_references`, `s2_search_author`, `s2_get_author`, `s2_get_author_papers`, `s2_recommend_papers` | ✅ Ready |
| **arXiv** | *(not yet implemented)* | 🔧 Stub |
| **ClinicalTrials** | `ct_search_condition`, `ct_search_intervention`, `ct_get_study`, `ct_search_studies` | ✅ Ready |
| **ChEMBL** | `chembl_search_compounds`, `chembl_get_compound`, `chembl_get_drug_mechanism`, `chembl_get_bioactivity` | ✅ Ready |
| **PubChem** | `pubchem_search_compound`, `pubchem_get_compound`, `pubchem_get_description`, `pubchem_search_smiles`, `pubchem_get_synonyms` | ✅ Ready |
| **UniProt** | `uniprot_search`, `uniprot_get_protein`, `uniprot_get_sequence`, `uniprot_search_gene`, `uniprot_get_function`, `uniprot_search_organism` | ✅ Ready |
| **KEGG** | `kegg_search_pathways`, `kegg_get_pathway`, `kegg_search_genes`, `kegg_get_linked_pathways`, `kegg_search_compounds` | ✅ Ready |
| **Reactome** | *(not yet implemented)* | 🔧 Stub |

---

## Next Steps

- **[PubMed Guide](pubmed-guide.md)** — Deep dive into PubMed tools
- **[API Reference](api-reference.md)** — Full tool schemas
- **[Configuration](configuration.md)** — Environment variables
- **[Troubleshooting](troubleshooting.md)** — Common connection issues
