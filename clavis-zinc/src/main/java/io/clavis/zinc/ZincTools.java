package io.clavis.zinc;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZincTools {
    private final ZincClient client;

    public ZincTools(ZincClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. zinc_search
        tools.add(new ZincTool(
                "zinc_search",
                "Search for substances in ZINC15 by name, SMILES, or property query.",
                Map.of("query", "The search query (e.g., 'aspirin', 'CC(=O)Oc1ccccc1C(=O)O')"),
                List.of("query"),
                args -> {
                    try {
                        return client.searchSubstances((String) args.get("query")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. zinc_get_compound
        tools.add(new ZincTool(
                "zinc_get_compound",
                "Retrieve detailed chemical metadata for a specific ZINC compound.",
                Map.of("zincId", "The ZINC ID of the compound (e.g., 'ZINC000000000053')"),
                List.of("zincId"),
                args -> {
                    try {
                        return client.getSubstanceDetails((String) args.get("zincId")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
