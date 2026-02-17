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

## Tools

### `epmc_search`
Search Europe PMC's collection. Supports query syntax for titles, authors, and sources.

**Example**: `author:"Smith J" cancer`

### `epmc_get_details`
Retrieve metadata, abstract, and external links for a specific article.

**Parameters**: `id` (e.g. "33116279"), `source` (default "MED")

### `epmc_get_citations`
Get articles that cite the specified publication.

### `epmc_get_references`
Fetch the bibliography of the specified publication.

## API Details
- **Base URL**: `https://www.ebi.ac.uk/europepmc/webservices/rest`
- **Rate Limit**: 10 requests per second
- **Status**: ✅ Ready

---

*See also: [PubMed Guide](pubmed-guide.md) | [API Reference](api-reference.md)*
