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

## API Details
- **Base URL**: `https://reactome.org/ContentService`
- **Rate Limit**: Generous
- **Key**: None required
- **Docs**: [Reactome API](https://reactome.org/ContentService/)

## Status: 🔧 Stub — Implementation coming soon

---

*See also: [KEGG Guide](kegg-guide.md) | [API Reference](api-reference.md)*
