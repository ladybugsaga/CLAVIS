# Reactome Server Guide

## Overview
Reactome is a curated database of biological pathways and reactions, covering human biology and extending to 80+ other species through orthology projections.

## Features
- Pathway search and browsing
- Reaction-level detail
- Pathway diagrams and visualizations
- Interactome data
- Disease-pathway associations

## Setup
```bash
mvn clean install -pl clavis-core,clavis-reactome
```

```json
{"mcpServers": {"clavis-reactome": {"command": "java", "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-reactome/target/clavis-reactome-1.0.0-SNAPSHOT.jar"]}}}
```

## Tools

### `reactome_search`
Search Reactome for pathways, reactions, and entities by keyword.

### `reactome_get_pathway`
Get detailed information about a pathway by stable ID (e.g. `R-HSA-1640170`).

### `reactome_get_participants`
Get molecular participants (proteins, compounds) in a pathway or reaction.

### `reactome_get_pathways_for_entity`
Find all pathways containing a gene (TP53), protein (P04637), or compound.

## API Details
- **Base URL**: `https://reactome.org/ContentService`
- **Rate Limit**: 5 requests per second
- **Status**: ✅ Ready

---

*See also: [KEGG Guide](kegg-guide.md) | [API Reference](api-reference.md)*
