# IntAct Guide

The `clavis-intact` module provides access to molecular interaction data from the **EBI IntAct database**, one of the world's leading curated repositories for protein-protein and protein-DNA interactions.

---

## Capabilities

IntAct focuses on experimental data for molecular interactions, providing details on participants, experimental methods, and source publications. CLAVIS exposes two primary tools:

1.  **`intact_search_interactions`**: Search for binary molecular interactions by gene/protein name.
2.  **`intact_get_interactors`**: Search for interactors (proteins/genes) within the database.

---

## Tool Reference

### `intact_search_interactions`

Search for documented interactions involving a specific gene or protein.

**Parameters:**
- `query` (required): The search query (e.g., 'BRCA2', 'P53')
- `page` (optional): Page number for pagination (starts at 0)
- `pageSize` (optional): Number of results per page (default 10, max 100)

**Example:**
> "Find molecular interactions involving the protein **BRCA2**."
> 
> Tool call: `intact_search_interactions(query="BRCA2")`

---

### `intact_get_interactors`

Search for entities (proteins, genes, small molecules) that act as interactors in the database.

**Parameters:**
- `query` (required): Search keyword or name
- `page` (optional): Page number
- `pageSize` (optional): Page size

**Example:**
> "Search for interactors related to 'targeting protein'."
> 
> Tool call: `intact_get_interactors(query="Targeting protein")`

---

## Use Cases

- **Protein Network Analysis**: Use `intact_search_interactions` to identify binding partners for a target protein.
- **Experimental Verification**: Check if a suspected interaction has been experimentally verified in peer-reviewed literature.
- **Pathway Discovery**: Find novel interactors for specific genes to expand pathway knowledge.

---

## Configuration

The IntAct server uses the public IntAct REST API and requires no API keys.

| Variable | Description | Default |
|----------|-------------|---------|
| `INTACT_RATE_LIMIT` | Requests per second | `5.0` |

---

## Next Steps

- **[Uniprot Guide](uniprot-guide.md)** — Find protein metadata for IntAct results
- **[Reactome Guide](reactome-guide.md)** — Place interactions in the context of biological pathways
- **[PubMed Guide](pubmed-guide.md)** — Research the papers cited in IntAct data
