package io.clavis.zinc;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZincServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(ZincServer.class);

    private final ZincClient client;

    public ZincServer() {
        super("clavis-zinc", "1.0.0");
        this.client = new ZincClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            ZincTools toolFactory = new ZincTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} ZINC tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register ZINC tools", e);
        }
    }

    public static void main(String[] args) {
        new ZincServer().start();
    }
}
