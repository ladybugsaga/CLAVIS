package io.clavis.clinvar;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClinVarServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(ClinVarServer.class);

    private final ClinVarClient client;

    public ClinVarServer() {
        super("clavis-clinvar", "1.0.0");
        this.client = new ClinVarClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            ClinVarTools toolFactory = new ClinVarTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} ClinVar tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register ClinVar tools", e);
        }
    }

    public static void main(String[] args) {
        new ClinVarServer().start();
    }
}
