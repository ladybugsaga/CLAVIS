package io.clavis.arxiv;

import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS arXiv MCP Server implementation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ArxivServer extends MCPServer {

    public ArxivServer() {
        super("clavis-arxiv", "1.0.0");
    }

    @Override
    protected void registerTools() {
        ArxivClient client = new ArxivClient();
        ArxivTools tools = new ArxivTools(client);

        this.tools.add(tools.createSearchTool());
        this.tools.add(tools.createGetPaperTool());
        this.tools.add(tools.createSearchAuthorTool());
        this.tools.add(tools.createSearchCategoryTool());

        logger.info("CLAVIS arXiv MCP Server initialized with 4 tools");
    }

    public static void main(String[] args) {
        ArxivServer server = new ArxivServer();
        server.start();
    }
}
