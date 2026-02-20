package io.clavis.rxnorm;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RxNormTools {
    private final RxNormClient client;

    public RxNormTools(RxNormClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. rxnorm_search
        tools.add(new RxNormTool(
                "rxnorm_search",
                "Search for clinical drugs by name and get associated drug products.",
                Map.of("name", "The drug name to search for (e.g., 'Lipitor', 'azithromycin')"),
                List.of("name"),
                args -> {
                    try {
                        return client.search((String) args.get("name")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. rxnorm_get_rxcui
        tools.add(new RxNormTool(
                "rxnorm_get_rxcui",
                "Find an RxNorm Concept Unique Identifier (RxCUI) by drug name.",
                Map.of("name", "The drug name to find the RxCUI for"),
                List.of("name"),
                args -> {
                    try {
                        return client.getRxcui((String) args.get("name")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 3. rxnorm_get_properties
        tools.add(new RxNormTool(
                "rxnorm_get_properties",
                "Retrieve all properties for a specific RxNorm concept by RxCUI.",
                Map.of("rxcui", "The RxNorm Concept Unique Identifier (e.g., '159645')"),
                List.of("rxcui"),
                args -> {
                    try {
                        return client.getAllProperties((String) args.get("rxcui")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
