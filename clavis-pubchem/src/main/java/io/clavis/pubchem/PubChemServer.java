package io.clavis.pubchem;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubChemServer extends MCPServer {

    private static final Logger logger = LoggerFactory.getLogger(PubChemServer.class);

    public PubChemServer() {
        super("clavis-pubchem", "1.0.0");
    }

    @Override
    protected void registerTools() {
        PubChemTools toolDefinitions = new PubChemTools();
        tools.addAll(toolDefinitions.getAllTools());
        logger.info("CLAVIS PubChem MCP Server initialized with {} tools", tools.size());
    }

    public static void main(String[] args) {
        PubChemServer server = new PubChemServer();
        server.start();
    }
}
