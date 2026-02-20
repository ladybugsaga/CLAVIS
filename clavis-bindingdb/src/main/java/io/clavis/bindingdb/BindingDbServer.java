package io.clavis.bindingdb;

import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BindingDbServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(BindingDbServer.class);

    private final BindingDbClient client;

    public BindingDbServer() {
        super("clavis-bindingdb", "1.0.0");
        this.client = new BindingDbClient();
        registerTools();
    }

    @Override
    protected void registerTools() {
        try {
            BindingDbTools toolFactory = new BindingDbTools(client);
            tools.addAll(toolFactory.getAllTools());
            logger.info("Registered {} BindingDB tools", tools.size());
        } catch (Exception e) {
            logger.error("Failed to register BindingDB tools", e);
        }
    }

    public static void main(String[] args) {
        new BindingDbServer().start();
    }
}
