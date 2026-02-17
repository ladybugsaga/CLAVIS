# dbSNP Server Guide

## Overview
dbSNP (Database of Single Nucleotide Polymorphisms) contains 650M+ human genetic variants with allele frequencies, clinical significance, and gene associations. No API key required.

## Features
- SNP lookup by rsID
- Gene-based variant search
- Population allele frequencies (GnomAD, 1000 Genomes, TOPMED)
- Clinical significance & ClinVar links

## Setup
```bash
mvn clean install -pl clavis-core,clavis-dbsnp
```

```json
{"mcpServers": {"clavis-dbsnp": {"command": "java", "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-dbsnp/target/clavis-dbsnp-1.0.0-SNAPSHOT.jar"]}}}
```

## Tools

### `dbsnp_get_variant`
Get full details for a variant by rsID (e.g. `rs7412`, `rs429358`).

### `dbsnp_search_gene`
Find variants associated with a gene (e.g. `BRCA1`, `TP53`, `APOE`).

### `dbsnp_get_frequency`
Get population allele frequencies across studies.

### `dbsnp_get_clinical`
Get clinical significance, disease associations, and ClinVar accessions.

## API Details
- **Base URL**: `https://api.ncbi.nlm.nih.gov/variation/v0`
- **Rate Limit**: 3 requests per second (no key)
- **Status**: ✅ Ready

---

*See also: [PubMed Guide](pubmed-guide.md) | [API Reference](api-reference.md)*
