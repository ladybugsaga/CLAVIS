package io.clavis.rxnorm;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxNormServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(RxNormServer.class);

    private final RxNormClient client;

    public RxNormServer() {
        super("clavis-rxnorm", "1.0.0");
        this.client = new RxNormClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            RxNormTools toolFactory = new RxNormTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} RxNorm tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register RxNorm tools", e);
        }
    }

    public static void main(String[] args) {
        new RxNormServer().start();
    }
}
