package io.clavis.gtex;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GtexServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(GtexServer.class);

    private final GtexClient client;

    public GtexServer() {
        super("clavis-gtex", "1.0.0");
        this.client = new GtexClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            GtexTools toolFactory = new GtexTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} GTEx tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register GTEx tools", e);
        }
    }

    public static void main(String[] args) {
        new GtexServer().start();
    }
}
