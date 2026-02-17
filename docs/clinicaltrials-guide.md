# ClinicalTrials.gov Server Guide

## Overview

The CLAVIS ClinicalTrials server provides access to **ClinicalTrials.gov** — the world's largest database of clinical studies with **470K+ trials** registered worldwide.

**No API key required.** Uses the ClinicalTrials.gov API v2.

---

## Available Tools

### `ct_search_condition`
Search clinical trials by condition or disease.

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `condition` | string | ✅ | — | Disease/condition (e.g. "lung cancer") |
| `pageSize` | integer | ❌ | 10 | Max results |

### `ct_search_intervention`
Search trials by intervention or treatment.

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `intervention` | string | ✅ | — | Treatment name (e.g. "pembrolizumab") |
| `pageSize` | integer | ❌ | 10 | Max results |

### `ct_get_study`
Get full details for a specific trial by NCT ID.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `nctId` | string | ✅ | NCT ID (e.g. `NCT04267848`) |

### `ct_search_studies`
General keyword search with optional status filter.

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | Search keyword |
| `status` | string | ❌ | all | Filter: `RECRUITING`, `COMPLETED`, `ACTIVE_NOT_RECRUITING`, etc. |
| `pageSize` | integer | ❌ | 10 | Max results |

---

## Example Prompts

- *"Search for recruiting lung cancer clinical trials"*
- *"Find clinical trials testing pembrolizumab"*
- *"Get details for trial NCT04267848"*
- *"What clinical trials are studying CRISPR gene therapy?"*

---

*See also: [API Reference](api-reference.md) | [KEGG Guide](kegg-guide.md) | [PubChem Guide](pubchem-guide.md)*
