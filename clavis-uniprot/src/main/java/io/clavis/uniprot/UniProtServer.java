package io.clavis.uniprot;

import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS UniProt MCP Server implementation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class UniProtServer extends MCPServer {

    public UniProtServer() {
        super("clavis-uniprot", "1.0.0");
    }

    @Override
    protected void registerTools() {
        // TODO: Register uniprot tools
        logger.info("CLAVIS UniProt MCP Server initialized (stub)");
    }

    public static void main(String[] args) {
        UniProtServer server = new UniProtServer();
        server.start();
    }
}
