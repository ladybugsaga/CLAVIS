package io.clavis.alphafold;

import io.clavis.core.mcp.MCPServer;

/**
 * CLAVIS AlphaFold MCP Server implementation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 */
public class AlphaFoldServer extends MCPServer {

    public AlphaFoldServer() {
        super("clavis-alphafold", "1.0.0");
    }

    @Override
    protected void registerTools() {
        AlphaFoldClient client = new AlphaFoldClient();
        AlphaFoldTools tools = new AlphaFoldTools(client);

        this.tools.add(tools.createGetPredictionTool());

        logger.info("CLAVIS AlphaFold MCP Server initialized with 1 tool");
    }

    public static void main(String[] args) {
        AlphaFoldServer server = new AlphaFoldServer();
        server.start();
    }
}
