# Semantic Scholar Server Guide

## Overview

The CLAVIS Semantic Scholar server provides AI assistants with access to **Semantic Scholar** — an AI-powered research engine covering **200M+ academic papers** across all fields of science, with citation graph traversal and AI recommendations.

---

## Available Tools

| Tool | Description | Rate Limit |
|------|-------------|------------|
| `s2_search` | Search 200M+ papers with year/venue/open-access filters | 10 req/s |
| `s2_get_paper` | Fetch paper details by S2 ID, DOI, PMID, or ArXiv ID | 10 req/s |
| `s2_get_citations` | Get papers that cite a given paper (forward) | 10 req/s |
| `s2_get_references` | Get papers referenced by a given paper (backward) | 10 req/s |
| `s2_search_author` | Search for researchers by name | 10 req/s |
| `s2_get_author` | Get author profile (h-index, citations, affiliations) | 10 req/s |
| `s2_get_author_papers` | Get all papers by a specific author | 10 req/s |
| `s2_recommend_papers` | AI-powered paper recommendations from seed papers | 10 req/s |

---

## Setup

### 1. Get an API Key (Optional)

1. Visit [Semantic Scholar API](https://www.semanticscholar.org/product/api)
2. Request a free API key
3. Without a key you get 1 req/s; with a key you get 10 req/s

### 2. Configure

Add to your `.env` file:
```bash
S2_API_KEY=your_api_key_here
```

### 3. Build & Connect

```bash
mvn clean install -pl clavis-core,clavis-semanticscholar
```

Add to your MCP config:
```json
{
  "mcpServers": {
    "clavis-semanticscholar": {
      "command": "java",
      "args": [
        "-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener",
        "-jar",
        "/path/to/clavis-semanticscholar/target/clavis-semanticscholar-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "S2_API_KEY": "your_key"
      }
    }
  }
}
```

---

## Search Examples

### Basic Search
> "Search Semantic Scholar for transformer architecture papers"

### Year-Filtered
> "Find 2023-2024 papers on large language models"

The AI will use: `query: "large language models"` with `year: "2023-2024"`

### Venue-Specific
> "Search for papers on reinforcement learning published at NeurIPS"

The AI will use: `query: "reinforcement learning"` with `venue: "NeurIPS"`

### Open Access Only
> "Find open access papers on protein folding"

The AI will use: `query: "protein folding"` with `openAccess: true`

### By External ID
> "Get the paper with DOI 10.1038/s41586-021-03819-2"

The AI will use: `s2_get_paper` with `paperId: "DOI:10.1038/s41586-021-03819-2"`

### Cross-Database Lookup
> "Get the Semantic Scholar entry for PubMed paper 33116279"

The AI will use: `s2_get_paper` with `paperId: "PMID:33116279"`

---

## Smart Research Workflows

1. **Citation Graph Traversal**: Search → Pick a top paper → Use `s2_get_citations` and `s2_get_references` to explore the citation network.
2. **Author Discovery**: Find an interesting author → Use `s2_get_author` for their profile → `s2_get_author_papers` for complete bibliography.
3. **AI Recommendations**: Found 2-3 great papers → Use `s2_recommend_papers` with their IDs to discover similar work.
4. **Cross-Database**: Use `PMID:` prefix to look up PubMed papers on Semantic Scholar, or `ArXiv:` for preprints.

---

## Paper ID Formats

| Prefix | Example | Source |
|--------|---------|--------|
| *(none)* | `649def34f8be52c8b66281af98ae884c09aef38b` | Semantic Scholar ID |
| `DOI:` | `DOI:10.1038/s41586-021-03819-2` | Digital Object Identifier |
| `PMID:` | `PMID:33116279` | PubMed ID |
| `ArXiv:` | `ArXiv:2106.09685` | arXiv preprint ID |
| `CorpusId:` | `CorpusId:235422077` | S2 Corpus ID |

---

## Rate Limits

| Scenario | Limit |
|----------|-------|
| With API key | 10 requests/second |
| Without API key | 1 request/second |
| Max search results | 100 per query |

CLAVIS automatically handles rate limiting with a token bucket algorithm.

---

## Response Fields

Each paper returned includes:

| Field | Type | Description |
|-------|------|-------------|
| `paperId` | string | Semantic Scholar Paper ID |
| `title` | string | Article title |
| `abstract` | string | Full abstract text |
| `authors` | string[] | Author names |
| `journal` | string | Journal name |
| `year` | string | Publication year |
| `doi` | string | Digital Object Identifier |
| `citationCount` | number | Number of citing papers |
| `url` | string | Semantic Scholar URL |
| `fieldsOfStudy` | string[] | Computer Science, Medicine, etc. |
| `publicationTypes` | string[] | JournalArticle, Review, Conference, etc. |

---

## Tips for Best Results

1. **Use natural language** — S2's search understands full queries like "deep learning for drug discovery"
2. **Combine filters** — Year + venue narrows effectively
3. **Use citations** — One good paper leads to many via citations/references
4. **Try recommendations** — The AI recommender surfaces non-obvious connections
5. **Cross-reference with PubMed** — Use `PMID:` prefix to bridge databases

---

## Next Steps

- **[API Reference](api-reference.md)** — Full tool schemas
- **[PubMed Guide](pubmed-guide.md)** — Biomedical literature
- **[Troubleshooting](troubleshooting.md)** — Common issues
