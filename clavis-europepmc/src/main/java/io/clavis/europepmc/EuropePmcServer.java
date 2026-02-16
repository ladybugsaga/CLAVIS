package io.clavis.europepmc;

import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS Europe PMC MCP Server implementation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class EuropePmcServer extends MCPServer {

    public EuropePmcServer() {
        super("clavis-europepmc", "1.0.0");
    }

    @Override
    protected void registerTools() {
        // TODO: Register europepmc tools
        logger.info("CLAVIS Europe PMC MCP Server initialized (stub)");
    }

    public static void main(String[] args) {
        EuropePmcServer server = new EuropePmcServer();
        server.start();
    }
}
