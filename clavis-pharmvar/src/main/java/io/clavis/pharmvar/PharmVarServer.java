package io.clavis.pharmvar;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PharmVarServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(PharmVarServer.class);

    private final PharmVarClient client;

    public PharmVarServer() {
        super("clavis-pharmvar", "1.0.0");
        this.client = new PharmVarClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            PharmVarTools toolFactory = new PharmVarTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} PharmVar tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register PharmVar tools", e);
        }
    }

    public static void main(String[] args) {
        new PharmVarServer().start();
    }
}
