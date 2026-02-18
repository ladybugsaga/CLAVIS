package io.clavis.openfda;

import io.clavis.core.config.ConfigManager;
import io.clavis.core.mcp.MCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OpenFDA MCP Server.
 */
public class OpenFdaServer extends MCPServer {
    private static final Logger logger = LoggerFactory.getLogger(OpenFdaServer.class);

    public OpenFdaServer() {
        super("clavis-openfda", "1.0.0");
    }

    @Override
    protected void registerTools() {
        ConfigManager config = ConfigManager.getInstance();
        String apiKey = config.get("OPENFDA_API_KEY", "");
        
        OpenFdaClient client = new OpenFdaClient(apiKey);
        OpenFdaTools toolFactory = new OpenFdaTools(client);
        
        tools.addAll(toolFactory.getAllTools());
        logger.info("Registered {} OpenFDA tools", tools.size());
    }

    public static void main(String[] args) {
        OpenFdaServer server = new OpenFdaServer();
        server.start();
    }
}
