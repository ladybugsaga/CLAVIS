# Europe PMC Server Guide

## Overview
Europe PMC provides access to 40M+ life science publications including full-text open-access articles. Unlike PubMed, Europe PMC includes preprints, patents, and NHS guidelines.

## Features
- Open-access full-text search
- No API key required
- Preprint and patent coverage
- Cross-references to related databases

## Setup
```bash
mvn clean install -pl clavis-core,clavis-europepmc
```

Add to Claude config:
```json
{"mcpServers": {"clavis-europepmc": {"command": "java", "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-europepmc/target/clavis-europepmc-1.0.0-SNAPSHOT.jar"]}}}
```

## API Details
- **Base URL**: `https://www.ebi.ac.uk/europepmc/webservices/rest`
- **Rate Limit**: Generous (no key needed)
- **Formats**: JSON, XML
- **Docs**: [Europe PMC API](https://europepmc.org/RestfulWebService)

## Status: 🔧 Stub — Implementation coming soon

---

*See also: [PubMed Guide](pubmed-guide.md) | [API Reference](api-reference.md)*
