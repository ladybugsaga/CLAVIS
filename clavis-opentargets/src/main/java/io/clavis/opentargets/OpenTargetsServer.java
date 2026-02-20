package io.clavis.opentargets;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenTargetsServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(OpenTargetsServer.class);

    private final OpenTargetsClient client;

    public OpenTargetsServer() {
        super("clavis-opentargets", "1.0.0");
        this.client = new OpenTargetsClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            OpenTargetsTools toolFactory = new OpenTargetsTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} Open Targets tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register Open Targets tools", e);
        }
    }

    public static void main(String[] args) {
        new OpenTargetsServer().start();
    }
}
