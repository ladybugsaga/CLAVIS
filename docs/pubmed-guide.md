# PubMed Server Guide

## Overview

The CLAVIS PubMed server provides AI assistants with access to **PubMed** — the world's largest biomedical literature database with over 36 million citations from MEDLINE, life science journals, and online books.

---

## Available Tools

| Tool | Description | Rate Limit |
|------|-------------|------------|
| `search_pubmed` | Full-text search across 36M+ papers | 10 req/s |
| `get_pubmed_paper` | Fetch single paper by PMID | 10 req/s |
| `get_related_papers` | Find related papers via citation links | 10 req/s |

---

## Setup

### 1. Get an NCBI API Key

1. Visit [NCBI Settings](https://www.ncbi.nlm.nih.gov/account/settings/)
2. Create a free account or sign in
3. Scroll to "API Key Management" → Create
4. Copy the key

### 2. Configure

Add to your `.env` file:
```bash
NCBI_API_KEY=your_api_key_here
NCBI_EMAIL=your_email@university.edu
```

### 3. Build & Connect

```bash
mvn clean install -pl clavis-core,clavis-pubmed
```

Add to Claude Desktop config:
```json
{
  "mcpServers": {
    "pubmed": {
      "command": "java",
      "args": ["-jar", "/path/to/clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar"],
      "env": {
        "NCBI_API_KEY": "your_key",
        "NCBI_EMAIL": "your_email"
      }
    }
  }
}
```

---

## Search Examples

### Basic Search
> "Search PubMed for CRISPR gene therapy"

### Author Search
> "Find papers by Jennifer Doudna on PubMed"

The AI will use: `query: "Doudna JA[Author]"`

### Journal-Specific
> "Search PubMed for cancer immunotherapy papers published in Nature"

The AI will use: `query: "cancer immunotherapy AND Nature[Journal]"`

### Date-Filtered
> "Find recent 2024 papers on mRNA vaccines"

The AI will use: `query: "mRNA vaccines AND 2024[Year]"`

### MeSH Terms
> "Search PubMed for papers classified under the Alzheimer Disease MeSH heading"

The AI will use: `query: "Alzheimer Disease[MeSH Terms]"`

### Complex Queries
> "Find clinical trials about pembrolizumab for non-small cell lung cancer"

The AI will use: `query: "pembrolizumab AND non-small cell lung cancer AND clinical trial[Publication Type]"`

---

## PubMed Query Syntax

| Syntax | Example | Meaning |
|--------|---------|---------|
| `AND` | `cancer AND therapy` | Both terms |
| `OR` | `cancer OR tumor` | Either term |
| `NOT` | `cancer NOT review` | Exclude term |
| `"..."` | `"gene editing"` | Exact phrase |
| `[Author]` | `Smith J[Author]` | Author field |
| `[Journal]` | `Nature[Journal]` | Journal field |
| `[Year]` | `2024[Year]` | Publication year |
| `[MeSH Terms]` | `Neoplasms[MeSH Terms]` | MeSH heading |
| `[Title]` | `CRISPR[Title]` | Title only |
| `[TIAB]` | `cancer[TIAB]` | Title + abstract |

---

## Rate Limits

| Scenario | Limit |
|----------|-------|
| With API key | 10 requests/second |
| Without API key | 3 requests/second |
| Maximum results per search | 10,000 |

CLAVIS automatically handles rate limiting with a token bucket algorithm — you don't need to worry about hitting limits.

---

## Response Fields

Each paper returned includes:

| Field | Type | Description |
|-------|------|-------------|
| `pmid` | string | PubMed ID |
| `title` | string | Article title |
| `abstract` | string | Full abstract text |
| `authors` | string[] | Author names |
| `journal` | string | Journal name |
| `publicationDate` | string | Publication year |
| `doi` | string | Digital Object Identifier |
| `url` | string | PubMed URL |

---

## Tips for Best Results

1. **Be specific** — Narrow queries return higher-quality results
2. **Use MeSH terms** — More precise than free text for medical concepts
3. **Combine fields** — `Author + Journal + Year` narrows effectively
4. **Start small** — Request 10–20 results, then refine
5. **Use related papers** — After finding one good paper, find related ones

---

## Next Steps

- **[API Reference](api-reference.md)** — Full tool schemas
- **[Europe PMC Guide](europepmc-guide.md)** — Open-access variant
- **[Troubleshooting](troubleshooting.md)** — Common issues
