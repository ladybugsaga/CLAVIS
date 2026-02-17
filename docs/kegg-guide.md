# KEGG Server Guide

## Overview

The CLAVIS KEGG server provides access to **KEGG** (Kyoto Encyclopedia of Genes and Genomes) — a comprehensive database of biological pathways, genes, compounds, diseases, and drugs.

**No API key required.** Rate limit: 3 requests/second.

---

## Available Tools

### `kegg_search_pathways`
Search pathways by keyword (e.g. "cancer", "glycolysis").

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | ✅ | Keyword to search |

### `kegg_get_pathway`
Get detailed entry info by KEGG ID. Works for pathways, compounds, diseases, drugs, and genes.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `keggId` | string | ✅ | ID (e.g. `hsa00010`, `C00002`, `H00001`, `D00001`, `hsa:7157`) |

### `kegg_search_genes`
Search genes by keyword across all organisms.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | ✅ | Gene name or keyword (e.g. "TP53", "BRCA1") |

### `kegg_get_linked_pathways`
Find all pathways associated with a specific gene.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `geneId` | string | ✅ | Gene ID (e.g. `hsa:7157` for TP53) |

### `kegg_search_compounds`
Search compounds by name or keyword.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | ✅ | Compound name (e.g. "aspirin", "glucose", "ATP") |

---

## Example Prompts

- *"Search KEGG for apoptosis pathways"*
- *"Get details for pathway hsa05200"*
- *"What pathways is TP53 (hsa:7157) involved in?"*
- *"Search KEGG for glucose-related compounds"*

---

## KEGG ID Prefixes

| Prefix | Database | Example |
|--------|----------|---------|
| `map` / `hsa` | Pathway | `hsa00010` |
| `C` | Compound | `C00002` (ATP) |
| `D` | Drug | `D00001` |
| `H` | Disease | `H00001` |
| `hsa:` | Human gene | `hsa:7157` (TP53) |

---

*See also: [API Reference](api-reference.md) | [ChEMBL Guide](chembl-guide.md)*
