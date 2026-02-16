package io.clavis.pubmed;

import io.clavis.core.config.ConfigManager;
import io.clavis.core.mcp.MCPServer;

/**
 * PubMed MCP Server implementation.
 *
 * <p>Provides tools for searching and retrieving biomedical papers
 * from PubMed's 36M+ paper database.</p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class PubMedServer extends MCPServer {

    private PubMedClient client;
    private PubMedTools toolFactory;

    public PubMedServer() {
        super("clavis-pubmed", "1.0.0");
    }

    @Override
    protected void registerTools() {
        ConfigManager config = ConfigManager.getInstance();
        String apiKey = config.get("NCBI_API_KEY", "");
        String email = config.get("NCBI_EMAIL", "");

        if (apiKey.isEmpty()) {
            logger.warn("NCBI_API_KEY not set — PubMed tools will use reduced rate limits (3 req/s)");
        }

        this.client = new PubMedClient(apiKey, email);
        this.toolFactory = new PubMedTools(client);

        tools.add(toolFactory.createSearchTool());
        tools.add(toolFactory.createGetPaperTool());
        tools.add(toolFactory.createRelatedPapersTool());

        logger.info("Registered {} PubMed tools", tools.size());
    }

    /**
     * Entry point for the PubMed MCP server.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        PubMedServer server = new PubMedServer();
        server.start();
    }
}
