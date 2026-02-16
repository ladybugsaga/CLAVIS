# API Reference

Complete reference for all CLAVIS MCP tools, their parameters, and response formats.

---

## PubMed Tools

### `search_pubmed`

Search PubMed's 36M+ biomedical papers.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | Search query (supports [PubMed query syntax](https://pubmed.ncbi.nlm.nih.gov/help/#search-tags)) |
| `maxResults` | number | ❌ | 20 | Max results to return (1–100) |

**Example request:**
```json
{
  "name": "search_pubmed",
  "arguments": {
    "query": "CRISPR cancer therapy 2024",
    "maxResults": 5
  }
}
```

**Response format:**
```json
{
  "query": "CRISPR cancer therapy 2024",
  "totalResults": 5,
  "papers": [
    {
      "pmid": "38123456",
      "title": "CRISPR-Based Approaches for Cancer Gene Therapy",
      "abstract": "Recent advances in CRISPR technology...",
      "journal": "Nature Reviews Cancer",
      "publicationDate": "2024",
      "doi": "10.1038/s41568-024-0001-2",
      "url": "https://pubmed.ncbi.nlm.nih.gov/38123456",
      "authors": ["Jane Smith", "John Doe"]
    }
  ]
}
```

**PubMed query syntax tips:**
| Query | Meaning |
|-------|---------|
| `cancer AND therapy` | Both terms |
| `"gene editing"` | Exact phrase |
| `Smith[Author]` | Author search |
| `Nature[Journal]` | Journal search |
| `2024[Year]` | Year filter |
| `review[Publication Type]` | Type filter |
| `cancer[MeSH Terms]` | MeSH heading |

---

### `get_pubmed_paper`

Retrieve a specific paper by PubMed ID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmid` | string | ✅ | PubMed ID |

**Example:**
```json
{
  "name": "get_pubmed_paper",
  "arguments": {
    "pmid": "33116279"
  }
}
```

**Response:** Same single paper format as above.

---

### `get_related_papers`

Find papers related to a given paper.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `pmid` | string | ✅ | — | Source paper PMID |
| `maxResults` | number | ❌ | 10 | Max related papers |

**Example:**
```json
{
  "name": "get_related_papers",
  "arguments": {
    "pmid": "33116279",
    "maxResults": 5
  }
}
```

---

## Europe PMC Tools (Stub)

### `search_europepmc`
Search Europe PMC's open-access literature collection.

### `get_europepmc_paper`
Retrieve a paper from Europe PMC by ID.

> [!NOTE]
> Europe PMC tools are currently stubs and will be implemented in a future release.

---

## Semantic Scholar Tools (Stub)

### `search_papers`
Search the Semantic Scholar corpus of 200M+ papers.

### `get_paper`
Get paper details including AI-generated TL;DR.

### `get_citations`
Get papers that cite a given paper.

---

## arXiv Tools (Stub)

### `search_arxiv`
Search arXiv preprints across physics, math, CS, and more.

### `get_arxiv_paper`
Get details of an arXiv paper by ID.

---

## ClinicalTrials.gov Tools (Stub)

### `search_trials`
Search clinical trial registrations.

### `get_trial`
Get detailed trial information by NCT number.

---

## DrugBank Tools (Stub)

### `search_drugs`
Search the DrugBank drug database.

### `get_drug`
Get comprehensive drug information.

### `get_interactions`
Get drug-drug interactions.

---

## PubChem Tools (Stub)

### `search_compounds`
Search PubChem's chemical compound database.

### `get_compound`
Get compound details including structure.

---

## UniProt Tools (Stub)

### `search_proteins`
Search UniProt's protein database.

### `get_protein`
Get protein sequence and annotation.

---

## KEGG Tools (Stub)

### `search_pathways`
Search KEGG biological pathways.

### `get_pathway`
Get pathway details and gene associations.

---

## Reactome Tools (Stub)

### `search_pathways`
Search Reactome pathways.

### `get_pathway`
Get pathway diagram and participant details.

---

## Error Responses

All tools return errors in this format:

```json
{
  "content": [
    {
      "type": "text",
      "text": "Error: Rate limit exceeded. Try again in 10 seconds."
    }
  ],
  "isError": true
}
```

Common error types:
| Error | Cause | Fix |
|-------|-------|-----|
| Rate limit exceeded | Too many requests | Wait and retry |
| API key not set | Missing configuration | Add key to `.env` |
| Network error | API unreachable | Check internet |
| Invalid PMID | Bad paper ID | Verify the ID |

---

## Next Steps

- **[PubMed Guide](pubmed-guide.md)** — Advanced PubMed usage
- **[Configuration](configuration.md)** — Set up API keys
- **[Troubleshooting](troubleshooting.md)** — Fix issues
