# Semantic Scholar Server Guide

## Overview
Semantic Scholar uses AI to analyze 200M+ academic papers, providing features like AI-generated TL;DRs, citation contexts, and influential citation detection.

## Features
- AI-generated paper summaries (TL;DR)
- Citation and reference graphs
- Author disambiguation
- Influential citation detection
- Fields of study classification

## Setup
```bash
mvn clean install -pl clavis-core,clavis-semanticscholar
```

```json
{"mcpServers": {"semanticscholar": {"command": "java", "args": ["-jar", "/path/to/clavis-semanticscholar/target/clavis-semanticscholar-1.0.0-SNAPSHOT.jar"], "env": {"SEMANTIC_SCHOLAR_API_KEY": "optional_key"}}}}
```

## API Details
- **Base URL**: `https://api.semanticscholar.org/graph/v1`
- **Rate Limit**: 1 req/s (free), 10 req/s (with key)
- **Key**: Optional but recommended — [Get key](https://www.semanticscholar.org/product/api#api-key)
- **Docs**: [Semantic Scholar API](https://api.semanticscholar.org/)

## Status: 🔧 Stub — Implementation coming soon

---

*See also: [PubMed Guide](pubmed-guide.md) | [API Reference](api-reference.md)*
