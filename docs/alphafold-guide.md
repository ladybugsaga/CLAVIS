# AlphaFold Server Guide

## Overview
AlphaFold Protein Structure Database provides open access to over 200 million protein structure predictions predicted by AlphaFold.

## Features
- Predicted 3D structures for UniProt accessions
- Confidence metrics (pLDDT)
- Direct links to PDB and mmCIF files
- Interactive PAE plots

## Setup
```bash
mvn clean install -pl clavis-core,clavis-alphafold
```

```json
{"mcpServers": {"clavis-alphafold": {"command": "java", "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-alphafold/target/clavis-alphafold-1.0.0-SNAPSHOT.jar"]}}}
```

## Tools

### `alphafold_get_prediction`
Get prediction details for a UniProt ID (e.g. `P04637`).
Returns:
- Global confidence score (pLDDT) and category
- Download links for PDB and mmCIF files
- PAE plot image URL
- Biological info (gene, organism, sequence range)

## API Details
- **Base URL**: `https://alphafold.ebi.ac.uk/api`
- **Rate Limit**: 5 requests per second
- **Status**: ✅ Ready

---

*See also: [UniProt Guide](uniprot-guide.md) | [API Reference](api-reference.md)*
