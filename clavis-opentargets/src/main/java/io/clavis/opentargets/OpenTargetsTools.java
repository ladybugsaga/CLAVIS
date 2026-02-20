package io.clavis.opentargets;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpenTargetsTools {
    private final OpenTargetsClient client;

    public OpenTargetsTools(OpenTargetsClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. opentargets_search
        tools.add(new OpenTargetsTool(
                "opentargets_search",
                "Search the Open Targets Platform for targets, diseases, or drugs.",
                Map.of("queryString", "The search query (e.g., 'BRCA1', 'asthma', 'aspirin')"),
                List.of("queryString"),
                args -> {
                    try {
                        return client.search((String) args.get("queryString")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. opentargets_get_target
        tools.add(new OpenTargetsTool(
                "opentargets_get_target",
                "Retrieve detailed information about a specific drug target by Ensembl ID.",
                Map.of("ensemblId", "The Ensembl gene ID (e.g., 'ENSG00000012048')"),
                List.of("ensemblId"),
                args -> {
                    try {
                        return client.getTarget((String) args.get("ensemblId")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 3. opentargets_get_disease
        tools.add(new OpenTargetsTool(
                "opentargets_get_disease",
                "Retrieve detailed information about a disease or phenotype by EFO ID.",
                Map.of("efoId", "The EFO ID (e.g., 'EFO_0000270')"),
                List.of("efoId"),
                args -> {
                    try {
                        return client.getDisease((String) args.get("efoId")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 4. opentargets_get_drug
        tools.add(new OpenTargetsTool(
                "opentargets_get_drug",
                "Retrieve detailed information about a drug or compound by ChEMBL ID.",
                Map.of("chemblId", "The ChEMBL ID (e.g., 'CHEMBL112')"),
                List.of("chemblId"),
                args -> {
                    try {
                        return client.getDrug((String) args.get("chemblId")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 5. opentargets_get_associations
        tools.add(new OpenTargetsTool(
                "opentargets_get_associations",
                "Retrieve disease associations for a specific target.",
                Map.of("ensemblId", "The Ensembl gene ID (e.g., 'ENSG00000012048')"),
                List.of("ensemblId"),
                args -> {
                    try {
                        return client.getAssociations((String) args.get("ensemblId")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
