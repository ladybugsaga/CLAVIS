# ClinicalTrials.gov Server Guide

## Overview
ClinicalTrials.gov is a registry of 470,000+ clinical studies from 220+ countries. It provides information about study design, enrollment, outcomes, and sponsor details.

## Features
- Search by condition, intervention, sponsor, or location
- Filter by study phase, status, and type
- Access enrollment and outcome data
- NCT number lookup

## Setup
```bash
mvn clean install -pl clavis-core,clavis-clinicaltrials
```

```json
{"mcpServers": {"clinicaltrials": {"command": "java", "args": ["-jar", "/path/to/clavis-clinicaltrials/target/clavis-clinicaltrials-1.0.0-SNAPSHOT.jar"]}}}
```

## API Details
- **Base URL**: `https://clinicaltrials.gov/api/v2`
- **Rate Limit**: Generous
- **Key**: None required
- **Docs**: [CT.gov API](https://clinicaltrials.gov/data-api/api)

## Status: 🔧 Stub — Implementation coming soon

---

*See also: [DrugBank Guide](drugbank-guide.md) | [API Reference](api-reference.md)*
