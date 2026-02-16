package io.clavis.pubchem;

import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS PubChem MCP Server implementation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class PubChemServer extends MCPServer {

    public PubChemServer() {
        super("clavis-pubchem", "1.0.0");
    }

    @Override
    protected void registerTools() {
        // TODO: Register pubchem tools
        logger.info("CLAVIS PubChem MCP Server initialized (stub)");
    }

    public static void main(String[] args) {
        PubChemServer server = new PubChemServer();
        server.start();
    }
}
