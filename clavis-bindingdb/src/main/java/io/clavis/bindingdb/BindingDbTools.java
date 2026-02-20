package io.clavis.bindingdb;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BindingDbTools {
    private final BindingDbClient client;

    public BindingDbTools(BindingDbClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. bindingdb_get_ligands_by_uniprot
        tools.add(new BindingDbTool(
                "bindingdb_get_ligands_by_uniprot",
                "Retrieve all ligands and binding affinities for a protein by its UniProt ID.",
                Map.of(
                    "uniprot", "The UniProt ID of the target protein (e.g., 'P35355')",
                    "cutoff", "Optional affinity cutoff in nM (e.g., '100')"
                ),
                List.of("uniprot"),
                args -> {
                    try {
                        return client.getLigandsByUniprot((String) args.get("uniprot"), (String) args.get("cutoff")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. bindingdb_get_targets_by_compound
        tools.add(new BindingDbTool(
                "bindingdb_get_targets_by_compound",
                "Retrieve protein targets and affinities for a specific small molecule compound (SMILES).",
                Map.of(
                    "smiles", "The compound SMILES string",
                    "similarity", "Optional similarity cutoff from 0.0 to 1.0 (default 1.0 for exact)"
                ),
                List.of("smiles"),
                args -> {
                    try {
                        return client.getTargetByCompound((String) args.get("smiles"), (String) args.get("similarity")).toString();
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
