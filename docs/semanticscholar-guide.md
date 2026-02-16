# Semantic Scholar MCP Server

Access 200M+ academic papers via AI-powered Semantic Scholar tools.

## Quick Start

```bash
# Build
mvn clean package -pl clavis-semanticscholar

# Run
java -jar clavis-semanticscholar/target/clavis-semanticscholar-1.0.0-SNAPSHOT.jar
```

## Configuration

| Variable | Required | Description |
|---|---|---|
| `S2_API_KEY` | No | API key for higher rate limits (1→10 req/s). Get one at [semanticscholar.org/product/api](https://www.semanticscholar.org/product/api) |

## Tools

### `s2_search`
Search papers with filters for year, venue, and open access.
```
"Search for 'transformer architecture' papers from 2023-2024"
```

### `s2_get_paper`
Get full paper details by S2 ID, DOI, PMID, or ArXiv ID.
```
"Get paper DOI:10.1038/s41586-021-03819-2"
```

### `s2_get_citations`
Get papers that cite a given paper (forward citations).
```
"Show me papers that cite paper abc123"
```

### `s2_get_references`
Get papers referenced by a given paper (backward citations).

### `s2_search_author`
Search for researchers by name.
```
"Search for author Geoffrey Hinton"
```

### `s2_get_author`
Get author profile with h-index, citation count, affiliations.

### `s2_get_author_papers`
List all papers by a specific author.

### `s2_recommend_papers`
AI-powered paper recommendations from seed papers.
```
"Recommend papers similar to abc123,def456"
```

## MCP Settings

Add to your `mcp_settings.json`:

```json
{
  "mcpServers": {
    "clavis-semanticscholar": {
      "command": "java",
      "args": [
        "-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener",
        "-jar",
        "/path/to/clavis-semanticscholar-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "S2_API_KEY": "your-key-here"
      },
      "disabled": false,
      "alwaysAllow": []
    }
  }
}
```
