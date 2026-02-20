package io.clavis.corepapers;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CorePapersServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(CorePapersServer.class);

    private final CorePapersClient client;

    public CorePapersServer() {
        super("clavis-core-papers", "1.0.0");
        this.client = new CorePapersClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            CorePapersTools toolFactory = new CorePapersTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} CORE tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register CORE tools", e);
        }
    }

    public static void main(String[] args) {
        new CorePapersServer().start();
    }
}
