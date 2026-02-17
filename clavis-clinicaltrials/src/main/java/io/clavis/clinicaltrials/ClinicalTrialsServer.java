package io.clavis.clinicaltrials;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClinicalTrialsServer extends MCPServer {

    private static final Logger logger = LoggerFactory.getLogger(ClinicalTrialsServer.class);

    public ClinicalTrialsServer() {
        super("clavis-clinicaltrials", "1.0.0");
    }

    @Override
    protected void registerTools() {
        ClinicalTrialsTools toolDefinitions = new ClinicalTrialsTools();
        tools.addAll(toolDefinitions.getAllTools());
        logger.info("CLAVIS ClinicalTrials MCP Server initialized with {} tools", tools.size());
    }

    public static void main(String[] args) {
        ClinicalTrialsServer server = new ClinicalTrialsServer();
        server.start();
    }
}
