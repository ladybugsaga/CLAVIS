# Quick Start — Get Running in 5 Minutes

## 1. Clone & Build (2 min)

```bash
git clone https://github.com/your-org/CLAVIS.git
cd CLAVIS
mvn clean install -DskipTests
```

## 2. Get an NCBI API Key (1 min)

1. Go to [NCBI Settings](https://www.ncbi.nlm.nih.gov/account/settings/)
2. Sign in (or create a free account)
3. Scroll to **API Key Management** → click **Create**
4. Copy the key

## 3. Configure (30 sec)

```bash
cp .env.example .env
```

Edit `.env`:
```
NCBI_API_KEY=your_api_key_here
NCBI_EMAIL=your_email@example.com
```

## 4. Connect to Claude Desktop (1 min)

Edit your Claude Desktop config:

| OS | Config Path |
|----|------------|
| macOS | `~/Library/Application Support/Claude/claude_desktop_config.json` |
| Linux | `~/.config/claude/claude_desktop_config.json` |
| Windows | `%APPDATA%\Claude\claude_desktop_config.json` |

Add:
```json
{
  "mcpServers": {
    "pubmed": {
      "command": "java",
      "args": ["-jar", "/absolute/path/to/CLAVIS/clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar"],
      "env": {
        "NCBI_API_KEY": "your_api_key",
        "NCBI_EMAIL": "your_email"
      }
    }
  }
}
```

## 5. Use It (30 sec)

Restart Claude Desktop. Try these prompts:

> "Search PubMed for recent CRISPR cancer therapy papers"

> "Get the paper with PMID 33116279 and explain the key findings"

> "Find papers related to this study on immunotherapy"

---

## What Just Happened?

```
You ──▶ Claude Desktop ──▶ CLAVIS PubMed Server ──▶ PubMed API
                                                        │
You ◀── Claude Desktop ◀── CLAVIS PubMed Server ◀──────┘
        (AI summary)        (structured data)     (36M+ papers)
```

Claude uses the `search_pubmed` tool to query PubMed's 36 million+ papers, gets structured results with titles, abstracts, authors, and DOIs, then synthesizes an intelligent answer.

---

## Next Steps

| Want to... | Read |
|-----------|------|
| Add more servers | [Usage Guide](usage.md) |
| See all config options | [Configuration](configuration.md) |
| Understand the architecture | [Architecture](architecture.md) |
| Fix connection issues | [Troubleshooting](troubleshooting.md) |
