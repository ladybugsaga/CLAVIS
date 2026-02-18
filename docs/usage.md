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

### Step 2: Add CLAVIS Unified Server

Edit (or create) the config file:

```json
{
  "mcpServers": {
    "clavis-unified": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/CLAVIS/clavis-unified/target/clavis-unified-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "NCBI_API_KEY": "your_key_here",
        "SEMANTIC_SCHOLAR_API_KEY": "your_key_here"
      }
    }
  }
}
```

### Step 3: Restart & Verify

Quit and reopen Claude Desktop. You should see the 🔧 tools icon. Try a query that spans multiple databases:

*"Find the mechanism of Metformin in ChEMBL and recent studies in PubMed."*

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
            "/path/to/CLAVIS/clavis-unified/target/clavis-unified-1.0.0-SNAPSHOT.jar"
          ]
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
   - **Name**: `clavis-unified`
   - **Command**: `java`
   - **Args**: `-jar /path/to/CLAVIS/clavis-unified/target/clavis-unified-1.0.0-SNAPSHOT.jar`

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
    "clavis-unified": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/CLAVIS/clavis-unified/target/clavis-unified-1.0.0-SNAPSHOT.jar"
      ]
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
    "clavis-unified": {
      "command": "java",
      "args": ["-jar", "/path/to/clavis-unified/target/clavis-unified-1.0.0-SNAPSHOT.jar"]
    }
  }
}
```

---

## Running Manually (Testing)

You can test any CLAVIS server directly from the terminal:

```bash
# Start the unified server
java -jar clavis-unified/target/clavis-unified-1.0.0-SNAPSHOT.jar
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

## Unified Process

Previously, CLAVIS ran as 12 independent servers. We transitioned to a **Unified Server** architecture to save RAM and simplify management. The `clavis-unified` module aggregates all tools into a single process.

If you still need to run an individual server for debugging, you can still do so by targeting its specific JAR in the submodule `target/` directory.

---

## Available Tools Per Server

| Server | Status |
|--------|--------|
| **clavis-unified** | ✅ Ready (Includes all 57+ tools) |

---

## Next Steps

- **[PubMed Guide](pubmed-guide.md)** — Deep dive into PubMed tools
- **[API Reference](api-reference.md)** — Full tool schemas
- **[Configuration](configuration.md)** — Environment variables
- **[Troubleshooting](troubleshooting.md)** — Common connection issues
