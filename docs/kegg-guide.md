# KEGG Server Guide

## Overview
KEGG (Kyoto Encyclopedia of Genes and Genomes) is a database for understanding biological systems, linking genomic and chemical information to higher-level functions.

## Features
- Pathway search and visualization
- Gene-pathway associations
- Disease-gene relationships
- Drug-target interactions
- Metabolic network analysis

## Setup
```bash
mvn clean install -pl clavis-core,clavis-kegg
```

```json
{"mcpServers": {"kegg": {"command": "java", "args": ["-jar", "/path/to/clavis-kegg/target/clavis-kegg-1.0.0-SNAPSHOT.jar"]}}}
```

## API Details
- **Base URL**: `https://rest.kegg.jp`
- **Rate Limit**: Moderate
- **Key**: None (free for academic use)
- **Docs**: [KEGG API](https://www.kegg.jp/kegg/rest/keggapi.html)

## Status: 🔧 Stub — Implementation coming soon

---

*See also: [Reactome Guide](reactome-guide.md) | [UniProt Guide](uniprot-guide.md)*
