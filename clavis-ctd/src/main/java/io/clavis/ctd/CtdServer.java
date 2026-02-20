package io.clavis.ctd;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CtdServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(CtdServer.class);

    private final CtdClient client;

    public CtdServer() {
        super("clavis-ctd", "1.0.0");
        this.client = new CtdClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            CtdTools toolFactory = new CtdTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} CTD tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register CTD tools", e);
        }
    }

    public static void main(String[] args) {
        new CtdServer().start();
    }
}
