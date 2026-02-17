package io.clavis.kegg;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KEGGServer extends MCPServer {

    private static final Logger logger = LoggerFactory.getLogger(KEGGServer.class);

    public KEGGServer() {
        super("clavis-kegg", "1.0.0");
    }

    @Override
    protected void registerTools() {
        KEGGTools toolDefinitions = new KEGGTools();
        tools.addAll(toolDefinitions.getAllTools());
        logger.info("CLAVIS KEGG MCP Server initialized with {} tools", tools.size());
    }

    public static void main(String[] args) {
        KEGGServer server = new KEGGServer();
        server.start();
    }
}
