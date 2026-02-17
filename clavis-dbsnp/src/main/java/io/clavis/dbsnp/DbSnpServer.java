package io.clavis.dbsnp;

import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS dbSNP MCP Server implementation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 */
public class DbSnpServer extends MCPServer {

    public DbSnpServer() {
        super("clavis-dbsnp", "1.0.0");
    }

    @Override
    protected void registerTools() {
        DbSnpClient client = new DbSnpClient();
        DbSnpTools tools = new DbSnpTools(client);

        this.tools.add(tools.createGetVariantTool());
        this.tools.add(tools.createSearchGeneTool());
        this.tools.add(tools.createGetFrequencyTool());
        this.tools.add(tools.createGetClinicalTool());

        logger.info("CLAVIS dbSNP MCP Server initialized with 4 tools");
    }

    public static void main(String[] args) {
        DbSnpServer server = new DbSnpServer();
        server.start();
    }
}
