# Installation Guide

## Prerequisites

| Requirement | Minimum | Recommended |
|------------|---------|-------------|
| **Java** | 17 (LTS) | 21 (LTS) |
| **Maven** | 3.8+ | 3.9+ |
| **Git** | 2.30+ | Latest |
| **OS** | Linux, macOS, Windows | Linux/macOS |
| **RAM** | 512 MB | 1 GB+ |
| **Disk** | 200 MB | 500 MB |

---

## Step 1: Install Java

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-21-jdk -y
java -version
```

### Linux (Fedora/RHEL)
```bash
sudo dnf install java-21-openjdk-devel -y
java -version
```

### macOS (Homebrew)
```bash
brew install openjdk@21
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
```

### Windows
1. Download the [Eclipse Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21) installer
2. Run the `.msi` installer, check "Add to PATH"
3. Open a new terminal and verify:
   ```powershell
   java -version
   ```

### Using SDKMAN (Cross-Platform)
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.2-tem
java -version
```

---

## Step 2: Install Maven

### Linux (Ubuntu/Debian)
```bash
sudo apt install maven -y
mvn -version
```

### macOS (Homebrew)
```bash
brew install maven
mvn -version
```

### Windows
1. Download [Maven 3.9.x](https://maven.apache.org/download.cgi) binary zip
2. Extract to `C:\Program Files\Maven`
3. Add `C:\Program Files\Maven\bin` to your `PATH` environment variable
4. Verify:
   ```powershell
   mvn -version
   ```

### Using SDKMAN
```bash
sdk install maven 3.9.9
mvn -version
```

---

## Step 3: Clone the Repository

```bash
git clone https://github.com/your-org/CLAVIS.git
cd CLAVIS
```

---

## Step 4: Configure API Keys

CLAVIS requires API keys for the biomedical databases you intend to use. Not all keys are required — only configure the servers you need.

### Create your `.env` file
```bash
cp .env.example .env
```

### Edit `.env` with your keys
```bash
nano .env   # or use your preferred editor
```

### Required keys per server

| Server | Key Variable | How to Get |
|--------|-------------|------------|
| **PubMed** | `NCBI_API_KEY`, `NCBI_EMAIL` | [NCBI API Key](https://ncbiinsights.ncbi.nlm.nih.gov/2017/11/02/new-api-keys-for-the-e-utilities/) |
| **Europe PMC** | None required | Free, no key needed |
| **Semantic Scholar** | `SEMANTIC_SCHOLAR_API_KEY` | [Semantic Scholar API](https://www.semanticscholar.org/product/api#api-key) |
| **arXiv** | None required | Free, no key needed |
| **ClinicalTrials.gov** | None required | Free, no key needed |

| **PubChem** | None required | Free, no key needed |
| **UniProt** | None required | Free, no key needed |
| **KEGG** | None required | Free for academic use |
| **Reactome** | None required | Free, no key needed |

> [!TIP]
> Start with PubMed — it's the most feature-complete server. You only need an NCBI API key (free, takes 30 seconds to register).

---

## Step 5: Build the Project

```bash
# Build all modules
mvn clean install

# Build only a specific server (e.g., PubMed)
cd clavis-pubmed && mvn clean install && cd ..
```

### Expected output
```
[INFO] Reactor Summary for CLAVIS Parent 1.0.0-SNAPSHOT:
[INFO] CLAVIS Parent ...................................... SUCCESS
[INFO] CLAVIS Core ........................................ SUCCESS
[INFO] CLAVIS PubMed MCP Server ........................... SUCCESS
[INFO] ... (8 more modules)
[INFO] BUILD SUCCESS
```

---

## Step 6: Verify Installation

```bash
# Run tests
mvn test

# Check a specific server JAR exists
ls -la clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar
```

---

## Upgrading

```bash
cd CLAVIS
git pull origin main
mvn clean install
```

---

## Uninstalling

```bash
# Remove from local Maven repository
rm -rf ~/.m2/repository/io/clavis

# Remove the project
rm -rf ~/CLAVIS
```

---

## Next Steps

- **[Quick Start](quickstart.md)** — Get running in 5 minutes
- **[Usage Guide](usage.md)** — Connect to Claude, ChatGPT, VS Code
- **[Configuration](configuration.md)** — All configuration options
- **[Troubleshooting](troubleshooting.md)** — Common issues and fixes
