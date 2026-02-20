package io.clavis.unified;

import io.clavis.core.config.ConfigManager;
import io.clavis.core.mcp.MCPServer;
import io.clavis.pubmed.PubMedClient;
import io.clavis.pubmed.PubMedTools;
import io.clavis.semanticscholar.SemanticScholarClient;
import io.clavis.semanticscholar.SemanticScholarTools;
import io.clavis.europepmc.EuropePmcClient;
import io.clavis.europepmc.EuropePmcTools;
import io.clavis.arxiv.ArxivClient;
import io.clavis.arxiv.ArxivTools;
import io.clavis.uniprot.UniProtClient;
import io.clavis.uniprot.UniProtTools;
import io.clavis.reactome.ReactomeClient;
import io.clavis.reactome.ReactomeTools;
import io.clavis.alphafold.AlphaFoldClient;
import io.clavis.alphafold.AlphaFoldTools;
import io.clavis.dbsnp.DbSnpClient;
import io.clavis.dbsnp.DbSnpTools;
import io.clavis.chembl.ChEMBLTools;
import io.clavis.pubchem.PubChemTools;
import io.clavis.kegg.KEGGTools;
import io.clavis.clinicaltrials.ClinicalTrialsTools;
import io.clavis.openfda.OpenFdaClient;
import io.clavis.openfda.OpenFdaTools;
import io.clavis.intact.IntActClient;
import io.clavis.intact.IntActTools;
import io.clavis.dailymed.DailyMedClient;
import io.clavis.dailymed.DailyMedTools;
import io.clavis.opentargets.OpenTargetsClient;
import io.clavis.opentargets.OpenTargetsTools;
import io.clavis.hmdb.HmdbClient;
import io.clavis.hmdb.HmdbTools;
import io.clavis.rxnorm.RxNormClient;
import io.clavis.rxnorm.RxNormTools;
import io.clavis.corepapers.CorePapersClient;
import io.clavis.corepapers.CorePapersTools;
import io.clavis.zinc.ZincClient;
import io.clavis.zinc.ZincTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unified MCP Server that aggregates all CLAVIS modules into a single process.
 * Reduces RAM usage and simplifies tool management.
 */
public class UnifiedServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(UnifiedServer.class);

    public UnifiedServer() {
        super("clavis-unified", "1.0.0");
    }

    @Override
    public void registerTools() {
        ConfigManager config = ConfigManager.getInstance();

        // 1. PubMed
        try {
            String pubmedKey = config.get("NCBI_API_KEY", "");
            String pubmedEmail = config.get("NCBI_EMAIL", "");
            var pubmedClient = new PubMedClient(pubmedKey, pubmedEmail);
            var pubmedTools = new PubMedTools(pubmedClient);
            tools.add(pubmedTools.createSearchTool());
            tools.add(pubmedTools.createGetPaperTool());
            tools.add(pubmedTools.createRelatedPapersTool());
            tools.add(pubmedTools.createTrackCitationsTool());
            tools.add(pubmedTools.createBatchRetrieveTool());
            tools.add(pubmedTools.createCheckRetractionsTool());
            tools.add(pubmedTools.createRelatedDatabaseLinksTool());
            tools.add(pubmedTools.createSearchByAuthorTool());
            logger.info("Registered PubMed tools");
        } catch (Exception e) {
            logger.error("Failed to register PubMed tools", e);
        }

        // 2. Europe PMC
        try {
            var epmcClient = new EuropePmcClient();
            var epmcTools = new EuropePmcTools(epmcClient);
            tools.add(epmcTools.createSearchTool());
            tools.add(epmcTools.createGetDetailsTool());
            tools.add(epmcTools.createGetCitationsTool());
            tools.add(epmcTools.createGetReferencesTool());
            logger.info("Registered Europe PMC tools");
        } catch (Exception e) {
            logger.error("Failed to register Europe PMC tools", e);
        }

        // 3. Semantic Scholar
        try {
            String s2Key = config.get("SEMANTIC_SCHOLAR_API_KEY", "");
            var s2Client = new SemanticScholarClient(s2Key);
            var s2Tools = new SemanticScholarTools(s2Client);
            tools.add(s2Tools.createSearchTool());
            tools.add(s2Tools.createGetPaperTool());
            tools.add(s2Tools.createGetCitationsTool());
            tools.add(s2Tools.createGetReferencesTool());
            tools.add(s2Tools.createSearchAuthorTool());
            tools.add(s2Tools.createGetAuthorTool());
            tools.add(s2Tools.createGetAuthorPapersTool());
            tools.add(s2Tools.createRecommendPapersTool());
            logger.info("Registered Semantic Scholar tools");
        } catch (Exception e) {
            logger.error("Failed to register Semantic Scholar tools", e);
        }

        // 4. arXiv
        try {
            var arxivClient = new ArxivClient();
            var arxivTools = new ArxivTools(arxivClient);
            tools.add(arxivTools.createSearchTool());
            tools.add(arxivTools.createGetPaperTool());
            tools.add(arxivTools.createSearchAuthorTool());
            tools.add(arxivTools.createSearchCategoryTool());
            logger.info("Registered arXiv tools");
        } catch (Exception e) {
            logger.error("Failed to register arXiv tools", e);
        }

        // 5. ChEMBL
        try {
            var chemblTools = new ChEMBLTools();
            tools.addAll(chemblTools.getAllTools());
            logger.info("Registered ChEMBL tools");
        } catch (Exception e) {
            logger.error("Failed to register ChEMBL tools", e);
        }

        // 6. PubChem
        try {
            var pubchemTools = new PubChemTools();
            tools.addAll(pubchemTools.getAllTools());
            logger.info("Registered PubChem tools");
        } catch (Exception e) {
            logger.error("Failed to register PubChem tools", e);
        }

        // 7. UniProt
        try {
            var uniprotClient = new UniProtClient();
            var uniprotTools = new UniProtTools(uniprotClient);
            tools.addAll(uniprotTools.getAllTools());
            logger.info("Registered UniProt tools");
        } catch (Exception e) {
            logger.error("Failed to register UniProt tools", e);
        }

        // 8. KEGG
        try {
            var keggTools = new KEGGTools();
            tools.addAll(keggTools.getAllTools());
            logger.info("Registered KEGG tools");
        } catch (Exception e) {
            logger.error("Failed to register KEGG tools", e);
        }

        // 9. Reactome
        try {
            var reactomeClient = new ReactomeClient();
            var reactomeTools = new ReactomeTools(reactomeClient);
            tools.add(reactomeTools.createSearchTool());
            tools.add(reactomeTools.createGetPathwayTool());
            tools.add(reactomeTools.createGetParticipantsTool());
            tools.add(reactomeTools.createGetPathwaysForEntityTool());
            logger.info("Registered Reactome tools");
        } catch (Exception e) {
            logger.error("Failed to register Reactome tools", e);
        }

        // 10. ClinicalTrials.gov
        try {
            var ctTools = new ClinicalTrialsTools();
            tools.addAll(ctTools.getAllTools());
            logger.info("Registered ClinicalTrials tools");
        } catch (Exception e) {
            logger.error("Failed to register ClinicalTrials tools", e);
        }

        // 11. AlphaFold
        try {
            var afClient = new AlphaFoldClient();
            var afTools = new AlphaFoldTools(afClient);
            tools.add(afTools.createGetPredictionTool());
            logger.info("Registered AlphaFold tools");
        } catch (Exception e) {
            logger.error("Failed to register AlphaFold tools", e);
        }

        // 12. dbSNP
        try {
            var dbsnpClient = new DbSnpClient();
            var dbsnpTools = new DbSnpTools(dbsnpClient);
            tools.add(dbsnpTools.createGetVariantTool());
            tools.add(dbsnpTools.createSearchGeneTool());
            tools.add(dbsnpTools.createGetFrequencyTool());
            tools.add(dbsnpTools.createGetClinicalTool());
            logger.info("Registered dbSNP tools");
        } catch (Exception e) {
            logger.error("Failed to register dbSNP tools", e);
        }

        // 13. OpenFDA
        try {
            String openFdaKey = config.get("OPENFDA_API_KEY", "");
            var openFdaClient = new OpenFdaClient(openFdaKey);
            var openFdaTools = new OpenFdaTools(openFdaClient);
            tools.addAll(openFdaTools.getAllTools());
            logger.info("Registered OpenFDA tools");
        } catch (Exception e) {
            logger.error("Failed to register OpenFDA tools", e);
        }

        // 14. IntAct
        try {
            var intactTools = new IntActTools();
            tools.addAll(intactTools.createAllTools());
            logger.info("Registered IntAct tools");
        } catch (Exception e) {
            logger.error("Failed to register IntAct tools", e);
        }

        // 15. DailyMed
        try {
            var dailymedClient = new DailyMedClient();
            var dailymedTools = new DailyMedTools(dailymedClient);
            tools.addAll(dailymedTools.getAllTools());
            logger.info("Registered DailyMed tools");
        } catch (Exception e) {
            logger.error("Failed to register DailyMed tools", e);
        }

        // 16. Open Targets
        try {
            var opentargetsClient = new OpenTargetsClient();
            var opentargetsTools = new OpenTargetsTools(opentargetsClient);
            tools.addAll(opentargetsTools.getAllTools());
            logger.info("Registered Open Targets tools");
        } catch (Exception e) {
            logger.error("Failed to register Open Targets tools", e);
        }

        // 17. HMDB
        try {
            var hmdbClient = new HmdbClient();
            var hmdbTools = new HmdbTools(hmdbClient);
            tools.addAll(hmdbTools.getAllTools());
            logger.info("Registered HMDB tools");
        } catch (Exception e) {
            logger.error("Failed to register HMDB tools", e);
        }

        // 18. RxNorm
        try {
            var rxnormClient = new RxNormClient();
            var rxnormTools = new RxNormTools(rxnormClient);
            tools.addAll(rxnormTools.getAllTools());
            logger.info("Registered RxNorm tools");
        } catch (Exception e) {
            logger.error("Failed to register RxNorm tools", e);
        }

        // 19. CORE
        try {
            var corePapersClient = new CorePapersClient();
            var corePapersTools = new CorePapersTools(corePapersClient);
            tools.addAll(corePapersTools.getAllTools());
            logger.info("Registered CORE tools");
        } catch (Exception e) {
            logger.error("Failed to register CORE tools", e);
        }

        // 20. ZINC
        try {
            var zincClient = new ZincClient();
            var zincTools = new ZincTools(zincClient);
            tools.addAll(zincTools.getAllTools());
            logger.info("Registered ZINC tools");
        } catch (Exception e) {
            logger.error("Failed to register ZINC tools", e);
        }

        logger.info("Unified MCP Server ready with {} total tools", tools.size());
    }

    public static void main(String[] args) {
        UnifiedServer server = new UnifiedServer();
        server.start();
    }
}
