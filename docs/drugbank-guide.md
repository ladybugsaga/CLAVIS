# DrugBank Server Guide

## Overview
DrugBank is a comprehensive drug database containing 16,000+ drugs with detailed chemical, pharmacological, and pharmaceutical data including drug targets and interactions.

## Features
- Drug search by name, category, or target
- Drug-drug interaction checking
- Target and enzyme information
- Pharmacological classification
- FDA approval status

## Setup
```bash
mvn clean install -pl clavis-core,clavis-drugbank
```

```json
{"mcpServers": {"drugbank": {"command": "java", "args": ["-jar", "/path/to/clavis-drugbank/target/clavis-drugbank-1.0.0-SNAPSHOT.jar"], "env": {"DRUGBANK_API_KEY": "your_key"}}}}
```

## API Details
- **Base URL**: `https://go.drugbank.com/api/v1`
- **Rate Limit**: 5 req/s
- **Key**: Required — [Academic signup](https://go.drugbank.com/public_users/sign_up)
- **Docs**: [DrugBank API](https://docs.drugbank.com/)

## Status: 🔧 Stub — Implementation coming soon

---

*See also: [PubChem Guide](pubchem-guide.md) | [ClinicalTrials Guide](clinicaltrials-guide.md)*
