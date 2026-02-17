# PubChem Server Guide

## Overview

The CLAVIS PubChem server provides access to **PubChem** — the world's largest open chemistry database with **100M+ compounds**, molecular properties, and chemical descriptions.

**No API key required.** Uses the PubChem PUG REST API.

---

## Available Tools

### `pubchem_search_compound`
Search compounds by name (e.g. "aspirin", "caffeine").

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | string | ✅ | Compound name |

### `pubchem_get_compound`
Get detailed properties by PubChem CID.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `cid` | integer | ✅ | PubChem Compound ID (e.g. `2244` for aspirin) |

### `pubchem_get_description`
Get textual descriptions/summaries from multiple sources.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `cid` | integer | ✅ | PubChem CID |

### `pubchem_search_smiles`
Search by SMILES chemical structure notation.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `smiles` | string | ✅ | SMILES string (e.g. `CC(=O)OC1=CC=CC=C1C(=O)O`) |

### `pubchem_get_synonyms`
Get all known names/synonyms for a compound.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `cid` | integer | ✅ | PubChem CID |

---

## Example Prompts

- *"Search PubChem for ibuprofen"*
- *"Get properties for PubChem compound 2244"*
- *"What are the synonyms for caffeine?"*
- *"Search PubChem by SMILES: CC(=O)OC1=CC=CC=C1C(=O)O"*

---

*See also: [API Reference](api-reference.md) | [ChEMBL Guide](chembl-guide.md)*
