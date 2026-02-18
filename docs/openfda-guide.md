# OpenFDA Guide

The `clavis-openfda` module provides access to the world's largest database of drug adverse events, recalls, and labeling via the **OpenFDA API**.

---

## Capabilities

OpenFDA aggregates data from the FDA's Adverse Event Reporting System (FAERS), drug labeling, and enforcement reports. CLAVIS exposes three primary tools:

1.  **`openfda_search_adverse_events`**: Search FAERS reports for side effects and drug-drug interactions.
2.  **`openfda_search_drug_labels`**: Search official SPL drug labeling (Structured Product Labeling).
3.  **`openfda_search_recalls`**: Search drug enforcement reports for recalls and market withdrawals.

---

## Tool Reference

### `openfda_search_adverse_events`

Search patient reports for suspected drug side effects.

**Parameters:**
- `query` (required): OpenFDA query syntax (e.g., `patient.drug.medicinalproduct:aspirin`)
- `limit` (optional): Max results to return (default 10)

**Example:**
> "Find recent adverse event reports for the drug **Vioxx**."
> 
> Tool call: `openfda_search_adverse_events(query="patient.drug.medicinalproduct:vioxx")`

---

### `openfda_search_drug_labels`

Search Structured Product Labeling (SPL) for warnings, usage, and dosage.

**Parameters:**
- `query` (required): OpenFDA query syntax (e.g., `openfda.brand_name:lipitor`)
- `limit` (optional): Max results (default 10)

**Example:**
> "Find the FDA label for **Humira** and summarize its warnings."
> 
> Tool call: `openfda_search_drug_labels(query="openfda.brand_name:humira")`

---

### `openfda_search_recalls`

Search for drug recalls by manufacturer or reason.

**Parameters:**
- `query` (required): OpenFDA query syntax (e.g., `reason_for_recall:contamination`)
- `limit` (optional): Max results (default 10)

**Example:**
> "Are there any recent recalls for products containing **Metformin**?"
> 
> Tool call: `openfda_search_recalls(query="product_description:metformin")`

---

## OpenFDA Query Syntax

OpenFDA uses a specific query syntax. Here are some common fields:

| Field | Description | Example |
|-------|-------------|---------|
| `patient.drug.medicinalproduct` | Drug name in reports | `medicinalproduct:aspirin` |
| `patient.reaction.reactionmeddrapt` | Side effect (MedDRA term) | `reactionmeddrapt:nausea` |
| `openfda.brand_name` | Drug brand name (Labels) | `brand_name:lipitor` |
| `openfda.generic_name` | Drug generic name (Labels) | `generic_name:ibuprofen` |
| `reason_for_recall` | Reason for recall | `reason_for_recall:label` |

**Logical operators:**
- `+` (AND): `medicinalproduct:aspirin+reactionmeddrapt:nausea`
- `,` (OR): `brand_name:lipitor,brand_name:atorvastatin`

---

## Configuration

OpenFDA allows limited anonymous access but recommends an API key for higher rate limits.

| Variable | Description | Default |
|----------|-------------|---------|
| `OPENFDA_API_KEY` | Your OpenFDA API key | — |

**Rate limits:**
- **Without key**: 4 requests per second
- **With key**: 40 requests per second

Get your free key at: [open.fda.gov/api-key](https://open.fda.gov/api-key/)
