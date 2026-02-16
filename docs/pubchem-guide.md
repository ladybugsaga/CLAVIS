# PubChem Server Guide

## Overview
PubChem is the world's largest free chemistry database with 100M+ compounds, 300M+ substances, and bioactivity data from 1M+ assays.

## Features
- Compound search by name, formula, or structure
- Molecular properties and descriptors
- Bioactivity data
- SMILES and InChI structure notation
- Safety and hazard information

## Setup
```bash
mvn clean install -pl clavis-core,clavis-pubchem
```

```json
{"mcpServers": {"pubchem": {"command": "java", "args": ["-jar", "/path/to/clavis-pubchem/target/clavis-pubchem-1.0.0-SNAPSHOT.jar"]}}}
```

## API Details
- **Base URL**: `https://pubchem.ncbi.nlm.nih.gov/rest/pug`
- **Rate Limit**: 5 req/s
- **Key**: None required
- **Docs**: [PUG REST](https://pubchem.ncbi.nlm.nih.gov/docs/pug-rest)

## Status: 🔧 Stub — Implementation coming soon

---

*See also: [DrugBank Guide](drugbank-guide.md) | [API Reference](api-reference.md)*
