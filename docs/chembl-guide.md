# ChEMBL Server Guide

## Overview

The CLAVIS ChEMBL server provides access to **ChEMBL** — a manually curated database of bioactive molecules with drug-like properties. It contains **2.4M+ compounds** and **15M+ bioactivity measurements**.

---

## Available Tools

| Tool | Description | Rate Limit |
|------|-------------|------------|
| `chembl_search_compounds` | Search compounds by name (e.g., "aspirin") | 5 req/s |
| `chembl_get_compound` | Get detailed compound info by ChEMBL ID | 5 req/s |
| `chembl_get_drug_mechanism` | Get mechanism of action and targets | 5 req/s |
| `chembl_get_bioactivity` | Get potency data (IC50, EC50) against targets | 5 req/s |

---

## Setup

### 1. No API Key Required
ChEMBL's API is free and open access. No API key is needed.

### 2. Build & Connect

```bash
mvn clean install -pl clavis-core,clavis-chembl
```

Add to your MCP config:
```json
{
  "mcpServers": {
    "clavis-chembl": {
      "command": "java",
      "args": [
        "-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener",
        "-jar",
        "/path/to/clavis-chembl/target/clavis-chembl-1.0.0-SNAPSHOT.jar"
      ]
    }
  }
}
```

---

## Examples

### Search for a Compound
> "Find information about aspirin in ChEMBL"

The AI will use: `chembl_search_compounds` with `query="aspirin"`.

### Get Drug Mechanism
> "How does aspirin work?"

The AI will use: `chembl_get_drug_mechanism` with `chemblId="CHEMBL25"`.

### Find Bioactivity Data
> "Find compounds that inhibit EGFR with high potency"

The AI will first find the target ID for EGFR (using UniProt or ChEMBL search), then use `chembl_get_bioactivity` with `targetChemblId="CHEMBL203"`.

---

## Rate Limits

| Scenario | Limit |
|----------|-------|
| All requests | 5 request/second |

CLAVIS automatically handles rate limiting using a token bucket algorithm.
