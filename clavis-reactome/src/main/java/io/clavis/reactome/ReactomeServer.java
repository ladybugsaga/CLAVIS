package io.clavis.reactome;

import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS Reactome MCP Server implementation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ReactomeServer extends MCPServer {

    public ReactomeServer() {
        super("clavis-reactome", "1.0.0");
    }

    @Override
    protected void registerTools() {
        // TODO: Register reactome tools
        logger.info("CLAVIS Reactome MCP Server initialized (stub)");
    }

    public static void main(String[] args) {
        ReactomeServer server = new ReactomeServer();
        server.start();
    }
}
