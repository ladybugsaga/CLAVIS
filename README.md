<div align="center">

<img src="docs/logo.png" alt="CLAVIS Logo" width="180" />

# CLAVIS

**Unlocking Biomedical Knowledge for AI**

*A Java-based ecosystem of MCP servers providing AI assistants with direct access to 10+ biomedical databases*

[![Build](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java](https://img.shields.io/badge/Java-17%2B-blue)]()
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)
[![MCP](https://img.shields.io/badge/MCP-compatible-purple)]()

[Quick Start](docs/quickstart.md) · [Get API Keys](docs/signup.md) · [Installation](docs/installation.md) · [Usage Guide](docs/usage.md)

</div>

---

## The Problem

Researchers waste hours manually searching disparate biomedical databases with different interfaces, query languages, and APIs. AI assistants hallucinate facts instead of looking them up.

## The Solution

CLAVIS (Latin for "key") gives AI assistants like **Claude**, **ChatGPT**, **Cursor**, and **VS Code** direct access to real biomedical data through the **Model Context Protocol (MCP)**.

```
You: "Find recent CRISPR cancer therapy papers"
  ↓
Claude + CLAVIS → PubMed API → 36M+ papers
  ↓
Claude: "Here are 10 relevant papers with summaries..."
```

---

## 🗄️ Supported Databases

| Server | Database | Description / Records | API Key | Status |
|--------|----------|-----------------------|---------|--------|
| `clavis-pubmed` | [PubMed](https://pubmed.ncbi.nlm.nih.gov/) | 36M+ papers, life science journals | [Optional](docs/signup.md) | ✅ Ready |
| `clavis-semanticscholar` | [Semantic Scholar](https://www.semanticscholar.org/) | 200M+ papers, AI-powered | [Optional](docs/signup.md) | ✅ Ready |
| `clavis-clinicaltrials` | [ClinicalTrials.gov](https://clinicaltrials.gov/) | 470K+ trials, world's largest DB | None | ✅ Ready |
| `clavis-chembl` | [ChEMBL](https://www.ebi.ac.uk/chembl/) | 2.4M+ bioactive compounds | None | ✅ Ready |
| `clavis-pubchem` | [PubChem](https://pubchem.ncbi.nlm.nih.gov/) | 100M+ compounds, chemical properties | None | ✅ Ready |
| `clavis-uniprot` | [UniProt](https://www.uniprot.org/) | 250M+ proteins, sequences/function | None | ✅ Ready |
| `clavis-kegg` | [KEGG](https://www.kegg.jp/) | 500K+ pathways, genes, genomes | None | ✅ Ready |
| `clavis-europepmc` | [Europe PMC](https://europepmc.org/) | 40M+ papers, patents, preprints | None | ✅ Ready |
| `clavis-arxiv` | [arXiv](https://arxiv.org/) | 2.4M+ preprints, AI/Physics | None | ✅ Ready |
| `clavis-reactome` | [Reactome](https://reactome.org/) | 15K+ pathways, diagram data | None | 🔧 Stub |
| `clavis-openfda` | [OpenFDA](https://open.fda.gov/apis) | 10M+ adverse event reports, recalls | None | 📜 Planned |
| `clavis-dailymed` | [DailyMed](https://dailymed.nlm.nih.gov/dailymed/services) | 140K+ official FDA drug labels | None | 📜 Planned |
| `clavis-rxnorm` | [RxNorm](https://rxnav.nlm.nih.gov) | 100K+ standardized drug names | None | 📜 Planned |
| `clavis-opentargets` | [Open Targets](https://platform.opentargets.org) | 60K+ gene/disease targets | None | 📜 Planned |
| `clavis-clinvar` | [ClinVar](https://www.ncbi.nlm.nih.gov/clinvar) | 2M+ genetic variants, disease links | None | 📜 Planned |
| `clavis-string` | [STRING](https://string-db.org) | 67M+ protein interaction networks | None | 📜 Planned |
| `clavis-alphafold` | [AlphaFold](https://alphafold.ebi.ac.uk) | 200M+ AI-predicted 3D structures | None | 📜 Planned |
| `clavis-pdb` | [PDB](https://www.rcsb.org) | 220K+ experimental 3D structures | None | 📜 Planned |
| `clavis-gwas` | [GWAS Catalog](https://www.ebi.ac.uk/gwas) | 500K+ gen-variant associations | None | 📜 Planned |
| `clavis-hpa` | [Human Protein Atlas](https://www.proteinatlas.org) | 20K+ proteins, tissue expression | None | 📜 Planned |
| `clavis-ensembl` | [Ensembl](https://rest.ensembl.org) | Full human/multi-species genomes | None | 📜 Planned |
| `clavis-biorxiv` | [bioRxiv/medRxiv](https://api.biorxiv.org) | 240K+ research preprints | None | 📜 Planned |
| `clavis-chebi` | [ChEBI](https://www.ebi.ac.uk/chebi) | 60K+ chemical biology metabolites | None | 📜 Planned |
| `clavis-intact` | [IntAct](https://www.ebi.ac.uk/intact) | 1M+ molecular interactions | None | 📜 Planned |
| `clavis-pharmgkb` | [PharmGKB](https://api.pharmgkb.org) | 50K+ pharmacogenomic relationships | None | 📜 Planned |
| `clavis-medlineplus` | [MedlinePlus](https://medlineplus.gov) | 40K+ patient-friendly help topics | None | 📜 Planned |
| `clavis-nci` | [NCI Thesaurus](https://evsrestapi.nci.nih.gov) | 170K+ authoritative cancer terms | None | 📜 Planned |
| `clavis-hmdb` | [HMDB](https://hmdb.ca) | 220K+ human body metabolites | None | 📜 Planned |
| `clavis-dbsnp` | [dbSNP](https://www.ncbi.nlm.nih.gov/snp) | 650M+ genetic variants/freqs | None | 📜 Planned |
| `clavis-biogrid` | [BioGRID](https://webservice.thebiogrid.org) | 2M+ curated bio-interactions | Free Key | 📜 Planned |

---

## 🚀 Quick Start

```bash
# 1. Clone and build
git clone https://github.com/ladybugsaga/CLAVIS.git && cd CLAVIS
mvn clean install

# 2. Configure (Optional: Add keys for higher rate limits)
cp .env.example .env
# Edit .env or skip if you want to use free tier

# 3. Connect to Claude Desktop
# Add to your claude_desktop_config.json:
```

```json
{
  "mcpServers": {
    "clavis-pubmed": {
      "command": "java",
      "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-pubmed-1.0.0-SNAPSHOT.jar"],
      "env": {"NCBI_API_KEY": "your_key", "NCBI_EMAIL": "your_email"}
    },
    "clavis-chembl": {
      "command": "java",
      "args": ["-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener", "-jar", "/path/to/clavis-chembl-1.0.0-SNAPSHOT.jar"]
    }
  }
}
```

**That's it!** Restart Claude Desktop and ask: *"Search PubMed for CRISPR gene therapy papers"*

→ Full guide: **[Quick Start](docs/quickstart.md)**

---

## 🏗️ Architecture

```
┌─────────────────┐    MCP/stdio    ┌──────────────────┐    HTTPS    ┌──────────┐
│   AI Assistant   │───────────────▶│  CLAVIS Server    │───────────▶│ PubMed   │
│  (Claude, etc.)  │◀───────────────│  (Java process)   │◀───────────│ API      │
└─────────────────┘                 └──────────────────┘            └──────────┘
                                            │
                                    ┌───────▼────────┐
                                    │  clavis-core   │
                                    │  Config, HTTP, │
                                    │  Cache, Models │
                                    └────────────────┘
```

- **11 Maven modules** — 1 core + 10 database servers
- **MCP over stdio** — JSON-RPC protocol for AI clients
- **Token bucket** rate limiting per API (auto-adjusts if no key provided)
- **Exponential backoff** retry for transient failures
- **In-memory TTL cache** for repeated queries

→ Full details: **[Architecture](docs/architecture.md)**

---

## 🛠️ Available Tools (PubMed)

Refer to **[API Reference](docs/api-reference.md)** for the complete list of tools and their usage.

---

## 📖 Documentation

### Getting Started
| Guide | Description |
|-------|-------------|
| **[Installation](docs/installation.md)** | Prerequisites, Java/Maven setup, API keys |
| **[API Keys & Signup](docs/signup.md)** | Where to get keys + Free tier info |
| **[Quick Start](docs/quickstart.md)** | Get running in 5 minutes |
| **[Usage Guide](docs/usage.md)** | Connect to Claude, VS Code, Cursor, ChatGPT |
| **[Configuration](docs/configuration.md)** | All environment variables |

### Server Guides
| Guide | Description |
|-------|-------------|
| **[PubMed](docs/pubmed-guide.md)** | Query syntax, examples, tips |
| **[Europe PMC](docs/europepmc-guide.md)** | Open-access literature |
| **[Semantic Scholar](docs/semanticscholar-guide.md)** | AI-powered features |
| **[arXiv](docs/arxiv-guide.md)** | Preprint search |
| **[ClinicalTrials](docs/clinicaltrials-guide.md)** | Trial registration data |
| **[ChEMBL](docs/chembl-guide.md)** | Bioactive compounds & targets |
| **[PubChem](docs/pubchem-guide.md)** | Chemical compounds |
| **[UniProt](docs/uniprot-guide.md)** | Protein sequences |
| **[KEGG](docs/kegg-guide.md)** | Biological pathways |
| **[Reactome](docs/reactome-guide.md)** | Pathway data |

### Reference & Operations
| Guide | Description |
|-------|-------------|
| **[API Reference](docs/api-reference.md)** | Complete tool schemas |
| **[Architecture](docs/architecture.md)** | System design & patterns |
| **[Performance](docs/performance.md)** | Tuning & optimization |
| **[Security](docs/security.md)** | API key management, privacy |
| **[Troubleshooting](docs/troubleshooting.md)** | Common issues & fixes |
| **[FAQ](docs/faq.md)** | Frequently asked questions |

### Development & Deployment
| Guide | Description |
|-------|-------------|
| **[Adding Servers](docs/contributing/adding-servers.md)** | Add a new database MCP server |
| **[Code Style](docs/contributing/code-style.md)** | Coding standards |
| **[Docker](docs/deployment/docker.md)** | Container deployment |
| **[Production](docs/deployment/production.md)** | Production hardening |
| **[Contributing](CONTRIBUTING.md)** | How to contribute |
| **[Changelog](CHANGELOG.md)** | Version history |

---

## 🧪 Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17+ (LTS) |
| Build | Apache Maven (Multi-Module) |
| HTTP | OkHttp 4.12 |
| JSON | Gson 2.11 |
| XML | Jackson XML 2.17 |
| Config | dotenv-java 3.0 |
| Logging | SLF4J 2.0 + Logback |
| Testing | JUnit 5 + Mockito 5 |
| CI/CD | GitHub Actions |

---

## 🤝 Contributing

We welcome contributions! See **[CONTRIBUTING.md](CONTRIBUTING.md)** for guidelines.

The easiest way to contribute is to **implement a stub MCP server** — see the **[Adding Servers](docs/contributing/adding-servers.md)** guide.

---

## 📄 License

MIT License — see **[LICENSE](LICENSE)** for details.

---

<div align="center">

**Built with ❤️ for the biomedical research community**

*CLAVIS — Every database, one protocol*

</div>