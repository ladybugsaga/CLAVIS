package io.clavis.chembl;

import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChEMBLTools {

    private final ChEMBLClient client;

    public ChEMBLTools() {
        this.client = new ChEMBLClient();
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();
        tools.add(searchCompounds());
        tools.add(getCompound());
        tools.add(getDrugMechanism());
        tools.add(getBioactivity());
        return tools;
    }

    private MCPTool searchCompounds() {
        return new ChEMBLTool(
            "chembl_search_compounds",
            "Search for compounds by name or synonym. Returns ChEMBL IDs and basic properties.",
            Map.of(
                "query", "string",
                "limit", "integer"
            ),
            List.of("query"),
            args -> {
                try {
                    String query = args.get("query").getAsString();
                    int limit = args.has("limit") ? args.get("limit").getAsInt() : 10;
                    String json = client.searchCompounds(query, limit);
                    return ChEMBLJsonParser.parseCompoundSearch(json).toString();
                } catch (IOException e) {
                    return "{\"error\": \"" + e.getMessage() + "\"}";
                }
            }
        );
    }

    private MCPTool getCompound() {
        return new ChEMBLTool(
            "chembl_get_compound",
            "Get detailed information about a compound by ChEMBL ID.",
            Map.of(
                "chemblId", "string"
            ),
            List.of("chemblId"),
            args -> {
                try {
                    String chemblId = args.get("chemblId").getAsString();
                    String json = client.getCompound(chemblId);
                    return ChEMBLJsonParser.parseCompound(json).toString();
                } catch (IOException e) {
                    return "{\"error\": \"" + e.getMessage() + "\"}";
                }
            }
        );
    }

    private MCPTool getDrugMechanism() {
        return new ChEMBLTool(
            "chembl_get_drug_mechanism",
            "Get mechanism of action and target information for a drug.",
            Map.of(
                "chemblId", "string"
            ),
            List.of("chemblId"),
            args -> {
                try {
                    String chemblId = args.get("chemblId").getAsString();
                    String json = client.getDrugMechanism(chemblId);
                    return ChEMBLJsonParser.parseMechanisms(json).toString();
                } catch (IOException e) {
                    return "{\"error\": \"" + e.getMessage() + "\"}";
                }
            }
        );
    }

    private MCPTool getBioactivity() {
        return new ChEMBLTool(
            "chembl_get_bioactivity",
            "Get bioactivity data (IC50, EC50, Ki) for a compound or against a target.",
            Map.of(
                "moleculeChemblId", "string",
                "targetChemblId", "string",
                "limit", "integer"
            ),
            List.of(), // At least one should be provided usually, but we'll handle validation
            args -> {
                try {
                    String moleculeChemblId = args.has("moleculeChemblId") ? args.get("moleculeChemblId").getAsString() : null;
                    String targetChemblId = args.has("targetChemblId") ? args.get("targetChemblId").getAsString() : null;
                    int limit = args.has("limit") ? args.get("limit").getAsInt() : 20;
                    
                    if (moleculeChemblId == null && targetChemblId == null) {
                        return "{\"error\": \"Must provide either moleculeChemblId or targetChemblId\"}";
                    }

                    String json = client.getBioactivity(targetChemblId, moleculeChemblId, limit);
                    return ChEMBLJsonParser.parseBioactivity(json).toString();
                } catch (IOException e) {
                    return "{\"error\": \"" + e.getMessage() + "\"}";
                }
            }
        );
    }
}
