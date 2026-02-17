# arXiv Server Guide

## Overview
arXiv hosts 2.4M+ preprints in physics, mathematics, computer science, biology, and more. Papers are freely accessible without any API key.

## Features
- Full-text search across 2.4M+ preprints
- Category-based browsing (cs.AI, q-bio.BM, etc.)
- Author and title search
- PDF access links

## Setup
```bash
mvn clean install -pl clavis-core,clavis-arxiv
```

```json
{"mcpServers": {"clavis-arxiv": {"command": "java", "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-arxiv/target/clavis-arxiv-1.0.0-SNAPSHOT.jar"]}}}
```

## API Details
- **Base URL**: `http://export.arxiv.org/api/query`
- **Protocol**: Atom/XML (OAI-PMH)
- **Rate Limit**: Moderate (be polite)
- **Key**: None required
- **Docs**: [arXiv API](https://info.arxiv.org/help/api/)

## Status: 🔧 Stub — Implementation coming soon

---

*See also: [PubMed Guide](pubmed-guide.md) | [API Reference](api-reference.md)*
