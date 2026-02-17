package io.clavis.chembl;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChEMBLServer extends MCPServer {

    private static final Logger logger = LoggerFactory.getLogger(ChEMBLServer.class);

    public ChEMBLServer() {
        super("clavis-chembl", "1.0.0");
    }

    @Override
    protected void registerTools() {
        ChEMBLTools toolDefinitions = new ChEMBLTools();
        tools.addAll(toolDefinitions.getAllTools());
        logger.info("CLAVIS ChEMBL MCP Server initialized with {} tools", tools.size());
    }

    public static void main(String[] args) {
        ChEMBLServer server = new ChEMBLServer();
        server.start();
    }
}
