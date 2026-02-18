package io.clavis.intact;

import io.clavis.core.mcp.MCPTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntActTools {
    private static final Logger logger = LoggerFactory.getLogger(IntActTools.class);
    private final IntActClient client;

    public IntActTools() {
        this.client = new IntActClient();
    }

    public List<MCPTool> createAllTools() {
        List<MCPTool> tools = new ArrayList<>();
        tools.add(createSearchInteractionsTool());
        tools.add(createGetInteractorsTool());
        return tools;
    }

    public MCPTool createSearchInteractionsTool() {
        return new IntActTool(
                "intact_search_interactions",
                "Search for binary molecular interactions by gene/protein name or query.",
                Map.of(
                        "query", "The search query (e.g., 'BRCA2', 'P53')",
                        "page", "Page number (default 0)",
                        "pageSize", "Page size (default 10, max 100)"
                ),
                List.of("query"),
                args -> {
                    String query = (String) args.get("query");
                    int page = args.containsKey("page") ? ((Double) args.get("page")).intValue() : 0;
                    int pageSize = args.containsKey("pageSize") ? ((Double) args.get("pageSize")).intValue() : 10;
                    try {
                        return client.searchInteractions(query, page, pageSize).toString();
                    } catch (Exception e) {
                        logger.error("Error in intact_search_interactions", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }

    public MCPTool createGetInteractorsTool() {
        return new IntActTool(
                "intact_get_interactors",
                "Search for interactors (proteins/genes) in the IntAct database.",
                Map.of(
                        "query", "The search query (e.g., 'Targeting protein')",
                        "page", "Page number (default 0)",
                        "pageSize", "Page size (default 10, max 100)"
                ),
                List.of("query"),
                args -> {
                    String query = (String) args.get("query");
                    int page = args.containsKey("page") ? ((Double) args.get("page")).intValue() : 0;
                    int pageSize = args.containsKey("pageSize") ? ((Double) args.get("pageSize")).intValue() : 10;
                    try {
                        return client.getInteractorsList(query, page, pageSize).toString();
                    } catch (Exception e) {
                        logger.error("Error in intact_get_interactors", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }
}
