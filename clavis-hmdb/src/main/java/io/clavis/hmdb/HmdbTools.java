package io.clavis.hmdb;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HmdbTools {
    private final HmdbClient client;

    public HmdbTools(HmdbClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. hmdb_search
        tools.add(new HmdbTool(
                "hmdb_search",
                "Search the Human Metabolome Database (HMDB). Currently requires a valid HMDB ID.",
                Map.of("query", "The search query or HMDB ID (e.g., 'HMDB0000122')"),
                List.of("query"),
                args -> {
                    try {
                        return client.search((String) args.get("query")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. hmdb_get_metabolite
        tools.add(new HmdbTool(
                "hmdb_get_metabolite",
                "Retrieve detailed information about a specific metabolite by HMDB ID.",
                Map.of("hmdbId", "The HMDB ID (e.g., 'HMDB0000001')"),
                List.of("hmdbId"),
                args -> {
                    try {
                        return client.getMetabolite((String) args.get("hmdbId")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
