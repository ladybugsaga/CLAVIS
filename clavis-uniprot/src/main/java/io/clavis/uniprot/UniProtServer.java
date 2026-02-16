package io.clavis.uniprot;

import io.clavis.core.mcp.MCPServer;
import io.clavis.core.mcp.MCPTool;

import java.util.List;

/**
 * CLAVIS UniProt MCP Server implementation.
 *
 * <p>
 * Provides access to UniProt's 250M+ protein entries with search,
 * protein details, sequence retrieval, gene/organism search, and
 * functional annotations.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class UniProtServer extends MCPServer {

    public UniProtServer() {
        super("clavis-uniprot", "1.0.0");
    }

    @Override
    protected void registerTools() {
        UniProtClient client = new UniProtClient();
        UniProtTools toolDefinitions = new UniProtTools(client);

        List<MCPTool> toolList = toolDefinitions.getAllTools();
        tools.addAll(toolList);

        logger.info("CLAVIS UniProt MCP Server initialized with {} tools.", toolList.size());
    }

    public static void main(String[] args) {
        UniProtServer server = new UniProtServer();
        server.start();
    }
}
