# API Key Signup & Registration Guide

CLAVIS is designed to work **out of the box** for most services without any API keys. However, adding keys unlocks higher rate limits and premium features.

---

## 🟢 No Signup Required (Free & Open)

These services work immediately without any configuration:

| Service | Content | Rate Limit |
|---------|---------|------------|
| **Europe PMC** | 40M+ Open Access Papers | Generous (High) |
| **arXiv** | 2.4M+ Preprints | Moderate (Be polite) |
| **ClinicalTrials.gov** | 470K+ Clinical Trials | Generous |
| **PubChem** | 118M+ Compounds | 5 requests/sec |
| **UniProt** | 250M+ Proteins | Generous |
| **KEGG** | Biological Pathways | Moderate |
| **Reactome** | Pathways & Reactions | Generous |

---

## 🟡 Optional Keys (Recommended for Performance)

Adding keys for these services is free and significantly improves performance (speed and rate limits).

### 1. PubMed (NCBI)
*Without a key, you are limited to 3 requests/second.*

*   **Benefit**: Increases limit to **10 requests/second**.
*   **Cost**: Free.
*   **How to get it**:
    1.  Log in or sign up at [NCBI](https://www.ncbi.nlm.nih.gov/account/).
    2.  Go to [Account Settings](https://www.ncbi.nlm.nih.gov/account/settings/).
    3.  Scroll to "API Key Management" and click **Create**.
    4.  Copy the long string of characters.
*   **Configuration**:
    ```bash
    NCBI_API_KEY=your_key_here
    NCBI_EMAIL=your_email@example.com
    ```

### 2. Semantic Scholar
*Without a key, you are limited to ~1 request/second.*

*   **Benefit**: Increases limit to **10+ requests/second**.
*   **Cost**: Free (Beta).
*   **How to get it**:
    1.  Visit the [Semantic Scholar API page](https://www.semanticscholar.org/product/api).
    2.  Click **Request API Key**.
    3.  Fill out the form (usually approved instantly for researchers/developers).
*   **Configuration**:
    ```bash
    SEMANTIC_SCHOLAR_API_KEY=your_key_here
    ```

---

## 🔴 Required Keys (Restricted Access)

These services **will not work** (or have very limited functionality) without an API key.

### 1. DrugBank
*Requires an academic or commercial license.*

*   **Benefit**: Access to high-quality curated drug data.
*   **Cost**: Free for Academics; Paid for Commercial.
*   **How to get it**:
    1.  **Academic**: Sign up at [DrugBank Academic](https://go.drugbank.com/public_users/sign_up).
    2.  **Commercial**: Contact [DrugBank Sales](https://www.drugbank.com/).
*   **Configuration**:
    ```bash
    DRUGBANK_API_KEY=your_key_here
    ```

---

## 🛡️ Best Practices

1.  **Start Free**: You don't need keys to start. Just run the server; CLAVIS automatically accepts missing keys and adjusts rate limits down to safe levels.
2.  **Keep Keys Secret**: Never commit your `.env` file to GitHub.
3.  **Monitor Usage**: If you hit rate limits (slow responses), consider getting a key for that specific service.
