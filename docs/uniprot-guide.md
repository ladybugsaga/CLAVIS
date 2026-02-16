# UniProt Server Guide

## Overview
UniProt is the most comprehensive protein database, containing 250M+ protein sequences with functional annotation, classification, and cross-references.

## Features
- Protein search by name, gene, organism, or function
- Sequence retrieval
- Functional annotation (GO terms)
- Cross-references to 200+ databases
- Protein family classification

## Setup
```bash
mvn clean install -pl clavis-core,clavis-uniprot
```

```json
{"mcpServers": {"uniprot": {"command": "java", "args": ["-jar", "/path/to/clavis-uniprot/target/clavis-uniprot-1.0.0-SNAPSHOT.jar"]}}}
```

## API Details
- **Base URL**: `https://rest.uniprot.org`
- **Rate Limit**: Generous
- **Key**: None required
- **Docs**: [UniProt API](https://www.uniprot.org/help/api)

## Status: 🔧 Stub — Implementation coming soon

---

*See also: [KEGG Guide](kegg-guide.md) | [API Reference](api-reference.md)*
