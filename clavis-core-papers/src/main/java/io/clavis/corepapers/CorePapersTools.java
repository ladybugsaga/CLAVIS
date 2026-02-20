package io.clavis.corepapers;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CorePapersTools {
    private final CorePapersClient client;

    public CorePapersTools(CorePapersClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. core_search_papers
        tools.add(new CorePapersTool(
                "core_search_papers",
                "Search for research papers in CORE (200M+ open access items).",
                Map.of(
                    "query", "The search query (e.g., 'artificial intelligence in medicine')",
                    "limit", "The number of results to return (default 10, max 100)"
                ),
                List.of("query"),
                args -> {
                    try {
                        String query = (String) args.get("query");
                        int limit = 10;
                        if (args.containsKey("limit")) {
                            Object limitObj = args.get("limit");
                            if (limitObj instanceof Number) {
                                limit = ((Number) limitObj).intValue();
                            } else if (limitObj instanceof String) {
                                limit = Integer.parseInt((String) limitObj);
                            }
                        }
                        return client.searchPapers(query, limit).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. core_get_paper_details
        tools.add(new CorePapersTool(
                "core_get_paper_details",
                "Retrieve full metadata for a specific paper by CORE ID.",
                Map.of("coreId", "The CORE ID of the paper (e.g., '141011')"),
                List.of("coreId"),
                args -> {
                    try {
                        return client.getPaperDetails((String) args.get("coreId")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
