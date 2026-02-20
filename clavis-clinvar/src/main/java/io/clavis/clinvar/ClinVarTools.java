package io.clavis.clinvar;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClinVarTools {
    private final ClinVarClient client;

    public ClinVarTools(ClinVarClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. clinvar_search
        tools.add(new ClinVarTool(
                "clinvar_search",
                "Search ClinVar for variants by gene, condition, or other terms (e.g., 'BRAF[gene]', 'Cystic fibrosis').",
                Map.of(
                    "query", "Entrez search query",
                    "max_results", "Optional maximum number of results (default 10)"
                ),
                List.of("query"),
                args -> {
                    try {
                        int max = args.containsKey("max_results") ? Integer.parseInt((String) args.get("max_results")) : 10;
                        return client.search((String) args.get("query"), max);
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. clinvar_get_summary
        tools.add(new ClinVarTool(
                "clinvar_get_summary",
                "Retrieve summary information for ClinVar Variation IDs (comma-separated).",
                Map.of("uids", "Comma-separated ClinVar UIDs"),
                List.of("uids"),
                args -> {
                    try {
                        return client.getSummary((String) args.get("uids"));
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 3. clinvar_get_details
        tools.add(new ClinVarTool(
                "clinvar_get_details",
                "Retrieve full ClinVar records for UIDs (XML format).",
                Map.of("uids", "Comma-separated ClinVar UIDs"),
                List.of("uids"),
                args -> {
                    try {
                        return client.getDetails((String) args.get("uids"));
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
