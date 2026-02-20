# API Reference

Complete reference for all CLAVIS MCP tools, their parameters, and response formats.

---

## PubMed Tools

### `search_pubmed`

Search PubMed's 36M+ biomedical papers.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | Search query (supports [PubMed query syntax](https://pubmed.ncbi.nlm.nih.gov/help/#search-tags)) |
| `maxResults` | number | ❌ | 20 | Max results to return (1–100) |
| `minYear` | string | ❌ | — | Filter by minimum publication year (e.g., "2020") |
| `maxYear` | string | ❌ | — | Filter by maximum publication year (e.g., "2024") |
| `articleType` | string | ❌ | — | Filter by article type (e.g., "Review", "Clinical Trial") |
| `freeFullText` | boolean | ❌ | false | Filter for free full text only |

**Example request:**
```json
{
  "name": "search_pubmed",
  "arguments": {
    "query": "CRISPR cancer therapy 2024",
    "maxResults": 5
  }
}
```

**Response format:**
```json
{
  "query": "CRISPR cancer therapy 2024",
  "totalResults": 5,
  "papers": [
    {
      "pmid": "38123456",
      "title": "CRISPR-Based Approaches for Cancer Gene Therapy",
      "abstract": "Recent advances in CRISPR technology...",
      "journal": "Nature Reviews Cancer",
      "publicationDate": "2024",
      "doi": "10.1038/s41568-024-0001-2",
      "url": "https://pubmed.ncbi.nlm.nih.gov/38123456",
      "authors": ["Jane Smith", "John Doe"],
      "meshTerms": ["Neoplasms", "CRISPR-Cas Systems"],
      "publicationTypes": ["Journal Article", "Review"]
    }
  ]
}
```

**PubMed query syntax tips:**
| Query | Meaning |
|-------|---------|
| `cancer AND therapy` | Both terms |
| `"gene editing"` | Exact phrase |
| `Smith[Author]` | Author search |
| `Nature[Journal]` | Journal search |
| `2024[Year]` | Year filter |
| `review[Publication Type]` | Type filter |
| `cancer[MeSH Terms]` | MeSH heading |

---

### `get_pubmed_paper`

Retrieve a specific paper by PubMed ID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmid` | string | ✅ | PubMed ID |

**Example:**
```json
{
  "name": "get_pubmed_paper",
  "arguments": {
    "pmid": "33116279"
  }
}
```

**Response:** Same single paper format as above.

---

### `get_related_papers`

Find papers related to a given paper.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `pmid` | string | ✅ | — | Source paper PMID |
| `maxResults` | number | ❌ | 10 | Max related papers |

**Example:**
```json
{
  "name": "get_related_papers",
  "arguments": {
    "pmid": "33116279",
    "maxResults": 5
  }
}
```

---

### `track_citations`

See who cites a paper and what it cites.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmid` | string | ✅ | PubMed ID |

**Example:**
```json
{
  "name": "track_citations",
  "arguments": {
    "pmid": "33116279"
  }
}
```

---

### `batch_retrieve`

Retrieve details for multiple papers at once.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmids` | string | ✅ | Comma-separated list of PMIDs |

**Example:**
```json
{
  "name": "batch_retrieve",
  "arguments": {
    "pmids": "33116279,38123456"
  }
}
```

---

### `check_retractions`

Check if a paper has been retracted or corrected.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmid` | string | ✅ | PubMed ID |

**Example:**
```json
{
  "name": "check_retractions",
  "arguments": {
    "pmid": "33116279"
  }
}
```

---

### `get_related_database_links`

Get links to associated NCBI databases (Genes, Proteins, Clinical Trials).

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pmid` | string | ✅ | PubMed ID |

---

### `search_by_author`

Find all papers by a specific researcher.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `author` | string | ✅ | Author name (e.g., "Watson JD") |

---

## Europe PMC Tools

### `epmc_search`

Search Europe PMC's collection of 40M+ biomedical papers, patents, and preprints.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | Search query (e.g. 'cancer', 'author:"Smith J"') |
| `pageSize` | integer | ❌ | 10 | Max results to return |

---

### `epmc_get_details`

Get full details for a specific Europe PMC article using ID and source.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `id` | string | ✅ | — | Article ID (e.g. '33116279') |
| `source` | string | ❌ | MED | Source: MED, PMC, PAT, AGR, etc. |

---

### `epmc_get_citations`

Get list of articles that cite the specified Europe PMC article.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `id` | string | ✅ | — | Article ID |
| `source` | string | ❌ | MED | Data source |
| `pageSize` | integer | ❌ | 10 | Max results |

---

### `epmc_get_references`

Get literature references for the specified Europe PMC article.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `id` | string | ✅ | — | Article ID |
| `source` | string | ❌ | MED | Data source |
| `pageSize` | integer | ❌ | 10 | Max results |

---

## Semantic Scholar Tools

### `s2_search`

Search Semantic Scholar's 200M+ papers across all fields.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | Search query |
| `maxResults` | number | ❌ | 10 | Max results (1–100) |
| `year` | string | ❌ | — | Year filter: `"2024"` or `"2020-2024"` |
| `venue` | string | ❌ | — | Venue filter (e.g., `"Nature"`, `"NeurIPS"`) |
| `openAccess` | boolean | ❌ | false | Only return open access papers |

---

## 💊 RxNorm (`clavis-rxnorm`)

Standardized drug nomenclature and identifier mapping through the NLM RxNav API.

| Tool | Parameters | Description |
|:---|:---|:---|
| `rxnorm_search` | `name` | Search for clinical drugs by name. |
| `rxnorm_get_rxcui` | `name` | Find an RxCUI by drug name. |
| `rxnorm_get_properties` | `rxcui` | Retrieve all properties for an RxNorm concept. |

---

## 🧬 PharmVar Variation (`clavis-pharmvar`)

Access to pharmacogene variation data, star alleles, and functional annotations from the Pharmacogene Variation Consortium.

| Tool | Parameters | Description |
|:---|:---|:---|
| `pharmvar_list_genes` | None | List all pharmacogenes defined in the PharmVar database. |
| `pharmvar_get_gene` | `symbol` | Retrieve detailed information for a specific gene by symbol. |
| `pharmvar_list_alleles` | None | List all active alleles across all genes in PharmVar. |
| `pharmvar_get_allele` | `identifier` | Retrieve details for a specific allele by PharmVar ID or name. |
| `pharmvar_get_allele_function` | `identifier` | Retrieve the CPIC Clinical Function for an allele. |

---

## 📊 BindingDB Affinities (`clavis-bindingdb`)

Access to 2.5M+ measured binding affinities (Ki, IC50, Kd) between drug-like molecules and protein targets.

| Tool | Parameters | Description |
|:---|:---|:---|
| `bindingdb_get_ligands_by_uniprot` | `uniprot`, `cutoff` | Retrieve all ligands and binding affinities for a protein by its UniProt ID. |
| `bindingdb_get_targets_by_compound` | `smiles`, `similarity` | Retrieve protein targets and affinities for a specific small molecule compound (SMILES). |

---

## 🧪 ZINC Compounds (`clavis-zinc`)

Access to 750M+ purchasable drug-like compounds for virtual screening via the ZINC15 API.

| Tool | Parameters | Description |
|:---|:---|:---|
| `zinc_search` | `query` | Search for substances in ZINC15 by name, SMILES, or property query. |
| `zinc_get_compound` | `zincId` | Retrieve detailed chemical metadata for a specific ZINC compound. |

---

## 📚 CORE Papers (`clavis-core-papers`)

Access to 200M+ open-access research papers from repositories worldwide via the CORE v3 API.

| Tool | Parameters | Description |
|:---|:---|:---|
| `core_search_papers` | `query`, `limit` | Search for research papers in CORE. |
| `core_get_paper_details` | `coreId` | Retrieve full metadata for a specific paper by CORE ID. |

---

## 🎯 Open Targets (`clavis-opentargets`)

---

### `s2_get_paper`

Retrieve a paper by S2 ID, DOI, PMID, or ArXiv ID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `paperId` | string | ✅ | Paper ID (prefix: `DOI:`, `PMID:`, `ArXiv:`, `CorpusId:`) |

---

### `s2_get_citations`

Get papers that cite a given paper (forward citations).

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `paperId` | string | ✅ | — | Paper ID |
| `maxResults` | number | ❌ | 20 | Max results |

---

### `s2_get_references`

Get papers referenced by a given paper (backward citations).

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `paperId` | string | ✅ | — | Paper ID |
| `maxResults` | number | ❌ | 20 | Max results |

---

### `s2_search_author`

Search for authors by name.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `name` | string | ✅ | — | Author name |
| `maxResults` | number | ❌ | 10 | Max results |

---

### `s2_get_author`

Get author profile including h-index, citation count, and affiliations.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `authorId` | string | ✅ | Semantic Scholar Author ID |

---

### `s2_get_author_papers`

Get all papers by a specific author.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `authorId` | string | ✅ | — | Author ID |
| `maxResults` | number | ❌ | 20 | Max results |

---

### `s2_recommend_papers`

AI-powered paper recommendations from seed papers.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `paperIds` | string | ✅ | — | Comma-separated S2 Paper IDs |
| `maxResults` | number | ❌ | 10 | Max recommendations |

---

## arXiv Tools (Stub)

### `search_arxiv`
Search arXiv preprints across physics, math, CS, and more.

### `get_arxiv_paper`
Get details of an arXiv paper by ID.

---

## ClinicalTrials.gov Tools

### `ct_search_condition`

Search clinical trials by condition or disease.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `condition` | string | ✅ | — | Disease/condition (e.g. "lung cancer") |
| `pageSize` | integer | ❌ | 10 | Max results |

---

### `ct_search_intervention`

Search trials by intervention or treatment.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `intervention` | string | ✅ | — | Treatment name (e.g. "pembrolizumab") |
| `pageSize` | integer | ❌ | 10 | Max results |

---

### `ct_get_study`

Get full details for a specific trial by NCT ID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `nctId` | string | ✅ | NCT identifier (e.g. `NCT04267848`) |

---

### `ct_search_studies`

General keyword search with optional status filter.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | Search keyword |
| `status` | string | ❌ | all | `RECRUITING`, `COMPLETED`, `ACTIVE_NOT_RECRUITING`, etc. |
| `pageSize` | integer | ❌ | 10 | Max results |

---

## ChEMBL Tools
 
 ### `chembl_search_compounds`
 
 Search for compounds by name or synonym.
 
 **Parameters:**
 | Parameter | Type | Required | Default | Description |
 |-----------|------|----------|---------|-------------|
 | `query` | string | ✅ | — | Search query (e.g. "aspirin") |
 | `limit` | integer | ❌ | 10 | Max results |
 
 ---
 
 ### `chembl_get_compound`
 
 Get detailed information about a compound by ChEMBL ID.
 
 **Parameters:**
 | Parameter | Type | Required | Description |
 |-----------|------|----------|-------------|
 | `chemblId` | string | ✅ | ChEMBL ID (e.g. "CHEMBL25") |
 
 ---
 
 ### `chembl_get_drug_mechanism`
 
 Get mechanism of action and target information for a drug.
 
 **Parameters:**
 | Parameter | Type | Required | Description |
 |-----------|------|----------|-------------|
 | `chemblId` | string | ✅ | ChEMBL ID |
 
 ---
 
 ### `chembl_get_bioactivity`
 
 Get bioactivity data (IC50, EC50, Ki) for a compound or against a target.
 
 **Parameters:**
 | Parameter | Type | Required | Default | Description |
 |-----------|------|----------|---------|-------------|
 | `moleculeChemblId` | string | ❌ | — | Filter by molecule |
 | `targetChemblId` | string | ❌ | — | Filter by target |
 | `limit` | integer | ❌ | 20 | Max results |


---

## PubChem Tools

### `pubchem_search_compound`

Search PubChem compounds by name.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | string | ✅ | Compound name (e.g. "aspirin", "caffeine") |

**Response:** Molecular formula, weight, IUPAC name, SMILES, InChIKey, XLogP, H-bond donors/acceptors.

---

### `pubchem_get_compound`

Get detailed compound properties by PubChem CID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `cid` | integer | ✅ | PubChem Compound ID (e.g. `2244` for aspirin) |

**Response:** Full property set including TPSA, complexity, exact mass, charge, isomeric SMILES.

---

### `pubchem_get_description`

Get textual descriptions/summaries from multiple sources.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `cid` | integer | ✅ | PubChem CID |

---

### `pubchem_search_smiles`

Search by SMILES chemical structure notation.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `smiles` | string | ✅ | SMILES string (e.g. `CC(=O)OC1=CC=CC=C1C(=O)O`) |

---

### `pubchem_get_synonyms`

Get all known synonyms (trade names, IUPAC, common names) for a compound.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `cid` | integer | ✅ | PubChem CID |

---

## UniProt Tools

### `uniprot_search`

Search UniProt's 250M+ protein database.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | Search query |
| `maxResults` | number | ❌ | 10 | Max results (1–100) |
| `organism` | string | ❌ | — | Taxonomy ID (e.g., `9606`) |
| `reviewed` | boolean | ❌ | false | Filter for Swiss-Prot entries |

---

### `uniprot_get_protein`

Get detailed protein information by accession ID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `accession` | string | ✅ | UniProt accession (e.g., `P01308`) |

---

### `uniprot_get_sequence`

Get the FASTA amino acid sequence for a protein.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `accession` | string | ✅ | UniProt accession |

---

### `uniprot_search_gene`

Search for proteins by gene name.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `geneName` | string | ✅ | — | Gene name (e.g., `BRCA1`) |
| `organism` | string | ❌ | — | Taxonomy ID |
| `maxResults` | number | ❌ | 10 | Max results |

---

### `uniprot_get_function`

Get functional annotation and subcellular location.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `accession` | string | ✅ | UniProt accession |

---

### `uniprot_search_organism`

Search proteins within a specific organism.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `organism` | string | ✅ | — | Organism name |
| `keyword` | string | ❌ | — | Optional keyword filter |
| `maxResults` | number | ❌ | 10 | Max results |

---

## KEGG Tools

### `kegg_search_pathways`

Search KEGG biological pathways by keyword.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | ✅ | Search keyword (e.g. "apoptosis", "cancer") |

---

### `kegg_get_pathway`

Get detailed pathway information by KEGG ID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `pathwayId` | string | ✅ | KEGG pathway ID (e.g. `hsa04210`) |

---

### `kegg_search_genes`

Search for genes related to a keyword.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | ✅ | Gene or keyword (e.g. "BRCA1", "insulin") |

---

### `kegg_get_linked_pathways`

Find pathways linked to a specific gene.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `geneId` | string | ✅ | KEGG gene ID (e.g. `hsa:672`) |

---

### `kegg_search_compounds`

Search KEGG chemical compounds.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | ✅ | Compound name (e.g. "glucose", "ATP") |

---

## Reactome Tools (Stub)

### `search_pathways`
Search Reactome pathways.

### `get_pathway`
Get pathway diagram and participant details.

---

## OpenFDA Tools

### `openfda_search_adverse_events`

Search FAERS reports for drug adverse events.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | OpenFDA query (e.g. `patient.drug.medicinalproduct:aspirin`) |
| `limit` | integer | ❌ | 10 | Max results |

---

### `openfda_search_drug_labels`

Search SPL drug labeling.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | OpenFDA query (e.g. `openfda.brand_name:lipitor`) |
| `limit` | integer | ❌ | 10 | Max results |

---

### `openfda_search_recalls`

Search drug enforcement reports.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | OpenFDA query (e.g. `reason_for_recall:contamination`) |
| `limit` | integer | ❌ | 10 | Max results |

---

---

## IntAct Tools

### `intact_search_interactions`
...
---

## DailyMed Tools

### `dailymed_search_spls`

Search drug labels (SPLs) by drug name. Returns SetIDs and titles.

**Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | string | ✅ | — | The drug name to search for (e.g., "aspirin") |
| `page` | number | ❌ | 1 | Page number |
| `pageSize` | number | ❌ | 20 | Page size (max 100) |

---

### `dailymed_get_spl_details`

Retrieve full metadata for a specific SPL by its SetID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `setId` | string | ✅ | The SetID of the SPL |

---

### `dailymed_search_drug_names`

Search for drug names matching a specific string fragment.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | ✅ | The drug name fragment to search for |

---

### `dailymed_get_drug_classes`

Get drug classes associated with a specific drug name.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `drugName` | string | ✅ | The drug name |

---

### `dailymed_get_ndcs_by_setid`

Retrieve National Drug Codes (NDCs) associated with a specific SetID.

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `setId` | string | ✅ | The SetID of the SPL |

---

## Error Responses

All tools return errors in this format:

```json
{
  "content": [
    {
      "type": "text",
      "text": "Error: Rate limit exceeded. Try again in 10 seconds."
    }
  ],
  "isError": true
}
```

Common error types:
| Error | Cause | Fix |
|-------|-------|-----|
| Rate limit exceeded | Too many requests | Wait and retry |
| API key not set | Missing configuration | Add key to `.env` |
| Network error | API unreachable | Check internet |
| Invalid PMID | Bad paper ID | Verify the ID |

---

## Next Steps

- **[PubMed Guide](pubmed-guide.md)** — Advanced PubMed usage
- **[Configuration](configuration.md)** — Set up API keys
- **[Troubleshooting](troubleshooting.md)** — Fix issues
