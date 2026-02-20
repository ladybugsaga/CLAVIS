package io.clavis.ensembl;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsemblServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(EnsemblServer.class);

    private final EnsemblClient client;

    public EnsemblServer() {
        super("clavis-ensembl", "1.0.0");
        this.client = new EnsemblClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            EnsemblTools toolFactory = new EnsemblTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} Ensembl tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register Ensembl tools", e);
        }
    }

    public static void main(String[] args) {
        new EnsemblServer().start();
    }
}
