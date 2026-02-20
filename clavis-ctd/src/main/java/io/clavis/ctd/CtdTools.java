package io.clavis.ctd;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CtdTools {
    private final CtdClient client;

    public CtdTools(CtdClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. ctd_get_chemical_interactions
        tools.add(new CtdTool(
                "ctd_get_chemical_interactions",
                "Retrieve curated chemical-gene, chemical-disease, and chemical-phenotype interactions for a chemical Name or ID from CTD.",
                Map.of("chemical", "Chemical name or identifier (e.g., 'Metformin', 'D008687')"),
                List.of("chemical"),
                args -> {
                    try {
                        return client.getChemicalInteractions((String) args.get("chemical"));
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. ctd_get_gene_interactions
        tools.add(new CtdTool(
                "ctd_get_gene_interactions",
                "Retrieve curated gene-chemical, gene-disease, and gene-phenotype interactions for a gene Symbol or ID from CTD.",
                Map.of("gene", "Gene symbol or identifier (e.g., 'BRCA2', '675')"),
                List.of("gene"),
                args -> {
                    try {
                        return client.getGeneInteractions((String) args.get("gene"));
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
