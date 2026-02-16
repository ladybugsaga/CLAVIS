package io.clavis.drugbank;

import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS DrugBank MCP Server implementation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class DrugBankServer extends MCPServer {

    public DrugBankServer() {
        super("clavis-drugbank", "1.0.0");
    }

    @Override
    protected void registerTools() {
        // TODO: Register drugbank tools
        logger.info("CLAVIS DrugBank MCP Server initialized (stub)");
    }

    public static void main(String[] args) {
        DrugBankServer server = new DrugBankServer();
        server.start();
    }
}
