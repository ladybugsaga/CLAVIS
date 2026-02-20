package io.clavis.hmdb;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HmdbServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(HmdbServer.class);

    private final HmdbClient client;

    public HmdbServer() {
        super("clavis-hmdb", "1.0.0");
        this.client = new HmdbClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            HmdbTools toolFactory = new HmdbTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} HMDB tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register HMDB tools", e);
        }
    }

    public static void main(String[] args) {
        new HmdbServer().start();
    }
}
