package io.clavis.kegg;

import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS KEGG MCP Server implementation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class KeggServer extends MCPServer {

    public KeggServer() {
        super("clavis-kegg", "1.0.0");
    }

    @Override
    protected void registerTools() {
        // TODO: Register kegg tools
        logger.info("CLAVIS KEGG MCP Server initialized (stub)");
    }

    public static void main(String[] args) {
        KeggServer server = new KeggServer();
        server.start();
    }
}
