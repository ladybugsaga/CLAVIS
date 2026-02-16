package io.clavis.semanticscholar;

import io.clavis.core.config.ConfigManager;
import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS Semantic Scholar MCP Server implementation.
 *
 * <p>
 * Provides tools for searching and retrieving academic papers
 * from Semantic Scholar's 200M+ paper corpus, with AI-powered
 * recommendations and rich citation graph traversal.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class SemanticScholarServer extends MCPServer {

    private SemanticScholarClient client;
    private SemanticScholarTools toolFactory;

    public SemanticScholarServer() {
        super("clavis-semanticscholar", "1.0.0");
    }

    @Override
    protected void registerTools() {
        ConfigManager config = ConfigManager.getInstance();
        String apiKey = config.get("S2_API_KEY", "");

        if (apiKey.isEmpty()) {
            logger.warn("S2_API_KEY not set — Semantic Scholar tools will use reduced rate limits (1 req/s)");
        }

        this.client = new SemanticScholarClient(apiKey);
        this.toolFactory = new SemanticScholarTools(client);

        tools.add(toolFactory.createSearchTool());
        tools.add(toolFactory.createGetPaperTool());
        tools.add(toolFactory.createGetCitationsTool());
        tools.add(toolFactory.createGetReferencesTool());
        tools.add(toolFactory.createSearchAuthorTool());
        tools.add(toolFactory.createGetAuthorTool());
        tools.add(toolFactory.createGetAuthorPapersTool());
        tools.add(toolFactory.createRecommendPapersTool());

        logger.info("Registered {} Semantic Scholar tools", tools.size());
    }

    /**
     * Entry point for the Semantic Scholar MCP server.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        SemanticScholarServer server = new SemanticScholarServer();
        server.start();
    }
}
