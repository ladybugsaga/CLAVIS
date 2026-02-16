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
| `minYear` | string | ❌ | — | Filter by minimum publication year (e.g., "2020") |
| `maxYear` | string | ❌ | — | Filter by maximum publication year (e.g., "2024") |
| `articleType` | string | ❌ | — | Filter by article type (e.g., "Review", "Clinical Trial") |
| `freeFullText` | boolean | ❌ | false | Filter for free full text only |

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
      "authors": ["Jane Smith", "John Doe"],
      "meshTerms": ["Neoplasms", "CRISPR-Cas Systems"],
      "publicationTypes": ["Journal Article", "Review"]
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

### `track_citations`

See who cites a paper and what it cites.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmid` | string | ✅ | PubMed ID |

**Example:**
```json
{
  "name": "track_citations",
  "arguments": {
    "pmid": "33116279"
  }
}
```

---

### `batch_retrieve`

Retrieve details for multiple papers at once.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmids` | string | ✅ | Comma-separated list of PMIDs |

**Example:**
```json
{
  "name": "batch_retrieve",
  "arguments": {
    "pmids": "33116279,38123456"
  }
}
```

---

### `check_retractions`

Check if a paper has been retracted or corrected.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmid` | string | ✅ | PubMed ID |

**Example:**
```json
{
  "name": "check_retractions",
  "arguments": {
    "pmid": "33116279"
  }
}
```

---

### `get_related_database_links`

Get links to associated NCBI databases (Genes, Proteins, Clinical Trials).

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmid` | string | ✅ | PubMed ID |

---

### `search_by_author`

Find all papers by a specific researcher.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `author` | string | ✅ | Author name (e.g., "Watson JD") |

---

## Europe PMC Tools (Stub)

### `search_europepmc`
Search Europe PMC's open-access literature collection.

### `get_europepmc_paper`
Retrieve a paper from Europe PMC by ID.

> [!NOTE]
> Europe PMC tools are currently stubs and will be implemented in a future release.

---

## Semantic Scholar Tools

### `s2_search`

Search Semantic Scholar's 200M+ papers across all fields.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | Search query |
| `maxResults` | number | ❌ | 10 | Max results (1–100) |
| `year` | string | ❌ | — | Year filter: `"2024"` or `"2020-2024"` |
| `venue` | string | ❌ | — | Venue filter (e.g., `"Nature"`, `"NeurIPS"`) |
| `openAccess` | boolean | ❌ | false | Only return open access papers |

---

### `s2_get_paper`

Retrieve a paper by S2 ID, DOI, PMID, or ArXiv ID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `paperId` | string | ✅ | Paper ID (prefix: `DOI:`, `PMID:`, `ArXiv:`, `CorpusId:`) |

---

### `s2_get_citations`

Get papers that cite a given paper (forward citations).

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `paperId` | string | ✅ | — | Paper ID |
| `maxResults` | number | ❌ | 20 | Max results |

---

### `s2_get_references`

Get papers referenced by a given paper (backward citations).

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `paperId` | string | ✅ | — | Paper ID |
| `maxResults` | number | ❌ | 20 | Max results |

---

### `s2_search_author`

Search for authors by name.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `name` | string | ✅ | — | Author name |
| `maxResults` | number | ❌ | 10 | Max results |

---

### `s2_get_author`

Get author profile including h-index, citation count, and affiliations.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `authorId` | string | ✅ | Semantic Scholar Author ID |

---

### `s2_get_author_papers`

Get all papers by a specific author.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `authorId` | string | ✅ | — | Author ID |
| `maxResults` | number | ❌ | 20 | Max results |

---

### `s2_recommend_papers`

AI-powered paper recommendations from seed papers.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `paperIds` | string | ✅ | — | Comma-separated S2 Paper IDs |
| `maxResults` | number | ❌ | 10 | Max recommendations |

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

## UniProt Tools

### `uniprot_search`

Search UniProt's 250M+ protein database.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | Search query |
| `maxResults` | number | ❌ | 10 | Max results (1–100) |
| `organism` | string | ❌ | — | Taxonomy ID (e.g., `9606`) |
| `reviewed` | boolean | ❌ | false | Filter for Swiss-Prot entries |

---

### `uniprot_get_protein`

Get detailed protein information by accession ID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `accession` | string | ✅ | UniProt accession (e.g., `P01308`) |

---

### `uniprot_get_sequence`

Get the FASTA amino acid sequence for a protein.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `accession` | string | ✅ | UniProt accession |

---

### `uniprot_search_gene`

Search for proteins by gene name.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `geneName` | string | ✅ | — | Gene name (e.g., `BRCA1`) |
| `organism` | string | ❌ | — | Taxonomy ID |
| `maxResults` | number | ❌ | 10 | Max results |

---

### `uniprot_get_function`

Get functional annotation and subcellular location.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `accession` | string | ✅ | UniProt accession |

---

### `uniprot_search_organism`

Search proteins within a specific organism.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `organism` | string | ✅ | — | Organism name |
| `keyword` | string | ❌ | — | Optional keyword filter |
| `maxResults` | number | ❌ | 10 | Max results |

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
