# UniProt Server Guide

## Overview

The CLAVIS UniProt server provides AI assistants with access to **UniProt** — the world's leading database of protein sequence and functional information, covering **250M+ proteins** across all branches of life.

---

## Available Tools

| Tool | Description | Rate Limit |
|------|-------------|------------|
| `uniprot_search` | Search proteins by name, gene, organism, etc. | 1 req/s |
| `uniprot_get_protein` | Get full protein details (sequence, function, domains) | 1 req/s |
| `uniprot_get_sequence` | Get just the FASTA sequence | 1 req/s |
| `uniprot_search_gene` | Search for proteins by gene name | 1 req/s |
| `uniprot_get_function` | Get functional annotations and subcellular location | 1 req/s |
| `uniprot_search_organism` | Search proteins within a specific organism | 1 req/s |

---

## Setup

### 1. No API Key Required

UniProt's REST API is free and open access. No API key is needed.
Rate limit is **1 request per second** (handled automatically by CLAVIS).

### 2. Build & Connect

```bash
mvn clean install -pl clavis-core,clavis-uniprot
```

Add to your MCP config:
```json
{
  "mcpServers": {
    "clavis-uniprot": {
      "command": "java",
      "args": [
        "-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener",
        "-jar",
        "/path/to/clavis-uniprot/target/clavis-uniprot-1.0.0-SNAPSHOT.jar"
      ]
    }
  }
}
```

---

## Search Examples

### Basic Search
> "Search UniProt for insulin"

### Organism-Specific
> "Find the EGFR protein in humans"

The AI will use: `uniprot_search` with `query="EGFR"` and `organism="9606"` (Human).

### Gene Search
> "Find proteins encoded by the BRCA1 gene"

The AI will use: `uniprot_search_gene` with `geneName="BRCA1"`.

### Sequence Retrieval
> "Get the amino acid sequence for protein P01308"

The AI will use: `uniprot_get_sequence` with `accession="P01308"`.

### Function Lookup
> "What is the function of the TP53 protein?"

The AI will use: `uniprot_get_function` with the accession for TP53.

---

## Rate Limits

| Scenario | Limit |
|----------|-------|
| All requests | 1 request/second |
| Max search results | 500 per query |

CLAVIS automatically handles rate limiting with a token bucket algorithm.

---

## Response Fields

Each protein returned includes:

| Field | Type | Description |
|-------|------|-------------|
| `accession` | string | Unique UniProt ID (e.g., P01308) |
| `entryId` | string | Mnemonic ID (e.g., INS_HUMAN) |
| `proteinName` | string | Full recommended name |
| `genes` | string[] | Gene names |
| `organism` | object | Scientific and common names |
| `sequence` | object | Sequence string and length |
| `function` | string | Functional description |
| `subcellularLocation` | string | Where it acts (e.g., Nucleus) |
| `features` | array | Domains, active sites, binding sites |

---

## Tips for Best Results

1. **Use Accessions**: If you know the ID (e.g., P12345), use `uniprot_get_protein` for the most accurate data.
2. **Filter by Organism**: Protein names like "kinase" exist in thousands of species. Always specify the organism if known.
3. **Use Swiss-Prot**: For high-quality, manually reviewed data, ask for "reviewed" entries (supported by `uniprot_search`).

---

## Next Steps

- **[API Reference](api-reference.md)** — Full tool schemas
- **[PubMed Guide](pubmed-guide.md)** — Biomedical literature
- **[Troubleshooting](troubleshooting.md)** — Common issues
