package io.clavis.pharmvar;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PharmVarTools {
    private final PharmVarClient client;

    public PharmVarTools(PharmVarClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. pharmvar_list_genes
        tools.add(new PharmVarTool(
                "pharmvar_list_genes",
                "List all pharmacogenes defined in the PharmVar database.",
                Map.of(),
                List.of(),
                args -> {
                    try {
                        return client.listGenes().toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. pharmvar_get_gene
        tools.add(new PharmVarTool(
                "pharmvar_get_gene",
                "Retrieve detailed information for a specific gene by symbol (e.g., 'CYP2D6').",
                Map.of("symbol", "The gene symbol (e.g., 'CYP2D6')"),
                List.of("symbol"),
                args -> {
                    try {
                        return client.getGene((String) args.get("symbol")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 3. pharmvar_list_alleles
        tools.add(new PharmVarTool(
                "pharmvar_list_alleles",
                "List all active alleles across all genes in PharmVar.",
                Map.of(),
                List.of(),
                args -> {
                    try {
                        return client.listAlleles().toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 4. pharmvar_get_allele
        tools.add(new PharmVarTool(
                "pharmvar_get_allele",
                "Retrieve details for a specific allele by its PharmVar ID or name (e.g., 'PV03044' or 'CYP2D6*1').",
                Map.of("identifier", "The PharmVar ID or allele name"),
                List.of("identifier"),
                args -> {
                    try {
                        return client.getAllele((String) args.get("identifier")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 5. pharmvar_get_allele_function
        tools.add(new PharmVarTool(
                "pharmvar_get_allele_function",
                "Retrieve the CPIC Clinical Function for an allele by its identifier.",
                Map.of("identifier", "The PharmVar ID or allele name"),
                List.of("identifier"),
                args -> {
                    try {
                        String function = client.getAlleleFunction((String) args.get("identifier"));
                        return "{\"function\": \"" + function + "\"}";
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
