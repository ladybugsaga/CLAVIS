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
        // TODO: Register arxiv tools
        logger.info("CLAVIS arXiv MCP Server initialized (stub)");
    }

    public static void main(String[] args) {
        ArxivServer server = new ArxivServer();
        server.start();
    }
}
