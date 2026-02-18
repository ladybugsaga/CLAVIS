# Quick Start — Get Running in 5 Minutes

## 1. Clone & Build (2 min)

```bash
git clone https://github.com/ladybugsaga/CLAVIS.git
cd CLAVIS
mvn clean install -DskipTests
```

## 2. Get API Keys (Optional)

1. **NCBI** (PubMed): [Settings](https://www.ncbi.nlm.nih.gov/account/settings/)
2. **Semantic Scholar**: [Settings](https://www.semanticscholar.org/product/api)

Copy your keys to the `.env` file for higher rate limits.

## 3. Configure (30 sec)

```bash
cp .env.example .env
```

Edit `.env`:
```
NCBI_API_KEY=your_key
SEMANTIC_SCHOLAR_API_KEY=your_key
```

## 4. Connect to Claude Desktop (1 min)

Edit your Claude Desktop config:

| OS | Config Path |
|----|------------|
| macOS | `~/Library/Application Support/Claude/claude_desktop_config.json` |
| Linux | `~/.config/claude/claude_desktop_config.json` |
| Windows | `%APPDATA%\Claude\claude_desktop_config.json` |

Add to your `mcpServers` configuration:

```json
{
  "mcpServers": {
    "clavis-unified": {
      "command": "java",
      "args": [
        "-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener",
        "-jar",
        "/absolute/path/to/CLAVIS/clavis-unified/target/clavis-unified-1.0.0-SNAPSHOT.jar"
      ]
    }
  }
}
```

## 5. Use It (30 sec)

Restart Claude Desktop. Try these prompts:

> "Research the drug **Metformin**. First, find its mechanism of action and bioactivity using **ChEMBL**. Then, search **PubMed** for clinical trials related to its use in 'aging'. Finally, check if there are any related protein pathways in **Reactome**."

---

## What Just Happened?

```
You ──▶ AI Client ──▶ CLAVIS Unified Server ──▶ Biomedical APIs
                                                     │
You ◀── AI Client ◀── CLAVIS Unified Server ◀────────┘
        (Summary)      (57+ tools combined)    (PubMed, ChEMBL, etc.)
```

The unified server combines all 14 modules into a single process, providing over 60 tools to your AI assistant while minimizing memory usage.

---

## Next Steps

| Want to... | Read |
|-----------|------|
| Add more servers | [Usage Guide](usage.md) |
| See all config options | [Configuration](configuration.md) |
| Understand the architecture | [Architecture](architecture.md) |
| Fix connection issues | [Troubleshooting](troubleshooting.md) |
