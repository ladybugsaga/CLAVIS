package io.clavis.gtex;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GtexTools {
    private final GtexClient client;

    public GtexTools(GtexClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. gtex_get_median_expression
        tools.add(new GtexTool(
                "gtex_get_median_expression",
                "Retrieve median gene expression across human tissues for a gene ID and optional tissue ID.",
                Map.of("geneId", "Gene identifier (e.g., 'BRCA2', 'ENSG00000139618')",
                       "tissueSiteDetailId", "Optional tissue identifier (e.g., 'Liver', 'Brain - Cortex')"),
                List.of("geneId"),
                args -> {
                    try {
                        return client.getMedianGeneExpression((String) args.get("geneId"), (String) args.get("tissueSiteDetailId"));
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. gtex_get_top_expressed_genes
        tools.add(new GtexTool(
                "gtex_get_top_expressed_genes",
                "Retrieve top expressed genes for a specified human tissue.",
                Map.of("tissueSiteDetailId", "Tissue identifier (e.g., 'Liver', 'Brain - Cortex')"),
                List.of("tissueSiteDetailId"),
                args -> {
                    try {
                        return client.getTopExpressedGenes((String) args.get("tissueSiteDetailId"));
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 3. gtex_get_egenes
        tools.add(new GtexTool(
                "gtex_get_egenes",
                "Retrieve significant eGenes (associated with genetic variation) for a gene and/or tissue.",
                Map.of("geneId", "Optional gene identifier (e.g., 'BRCA2')",
                       "tissueSiteDetailId", "Optional tissue identifier (e.g., 'Liver')"),
                List.of(),
                args -> {
                    try {
                        return client.getEgenes((String) args.get("geneId"), (String) args.get("tissueSiteDetailId"));
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 4. gtex_get_eqtls
        tools.add(new GtexTool(
                "gtex_get_eqtls",
                "Retrieve significant single-tissue eQTL associations for a gene and/or tissue.",
                Map.of("geneId", "Optional gene identifier (e.g., 'BRCA2')",
                       "tissueSiteDetailId", "Optional tissue identifier (e.g., 'Liver')"),
                List.of(),
                args -> {
                    try {
                        return client.getSingleTissueEqtls((String) args.get("geneId"), (String) args.get("tissueSiteDetailId"));
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
