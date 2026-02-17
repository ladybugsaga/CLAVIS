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

## Tools

### `arxiv_search`
Search arXiv preprints by keyword. Supports field prefixes: `ti:` (title), `au:` (author), `cat:` (category).

### `arxiv_get_paper`
Get full details for a paper by its arXiv ID (e.g. `2301.12345`, `hep-ex/0307015`).

### `arxiv_search_author`
Find papers by a specific author name.

### `arxiv_search_category`
Browse papers by arXiv category (e.g. `cs.AI`, `cs.LG`, `quant-ph`, `q-bio.BM`), with optional keyword filter.

## API Details
- **Base URL**: `http://export.arxiv.org/api/query`
- **Rate Limit**: 1 request per second
- **Status**: ✅ Ready

---

*See also: [PubMed Guide](pubmed-guide.md) | [API Reference](api-reference.md)*
