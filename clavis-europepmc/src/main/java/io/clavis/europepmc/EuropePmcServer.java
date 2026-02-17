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
        EuropePmcClient client = new EuropePmcClient();
        EuropePmcTools tools = new EuropePmcTools(client);

        this.tools.add(tools.createSearchTool());
        this.tools.add(tools.createGetDetailsTool());
        this.tools.add(tools.createGetCitationsTool());
        this.tools.add(tools.createGetReferencesTool());

        logger.info("CLAVIS Europe PMC MCP Server initialized with 4 tools");
    }

    public static void main(String[] args) {
        EuropePmcServer server = new EuropePmcServer();
        server.start();
    }
}
