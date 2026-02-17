package io.clavis.alphafold;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.util.JsonUtils;

import java.io.IOException;

/**
 * MCP tools for AlphaFold Protein Structure Database.
 */
public class AlphaFoldTools {
    private final AlphaFoldClient client;
    private final AlphaFoldJsonParser parser;

    public AlphaFoldTools(AlphaFoldClient client) {
        this.client = client;
        this.parser = new AlphaFoldJsonParser();
    }

    public MCPTool createGetPredictionTool() {
        return new MCPTool() {
            @Override
            public String getName() { return "alphafold_get_prediction"; }

            @Override
            public String getDescription() {
                return "Get AlphaFold protein structure prediction for a UniProt ID (e.g. P04637). "
                     + "Returns prediction confidence (pLDDT), PDB/mmCIF file URLs, and metadata.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject uniprotId = new JsonObject();
                uniprotId.addProperty("type", "string");
                uniprotId.addProperty("description", "UniProt accession (e.g. 'P04637' or 'P04637-2')");
                properties.add("uniprotId", uniprotId);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "uniprotId");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String uniprotId = params.get("uniprotId").getAsString();
                    String json = client.getPrediction(uniprotId);
                    
                    // Respond with the first prediction (usually the best one)
                    JsonArray predictions = com.google.gson.JsonParser.parseString(json).getAsJsonArray();
                    if (predictions.size() > 0) {
                        return parser.formatPrediction(predictions.get(0).getAsJsonObject());
                    } else {
                        return JsonUtils.formatError("No structure prediction found for " + uniprotId);
                    }
                } catch (IOException e) {
                    return JsonUtils.formatError("Failed to get prediction: " + e.getMessage());
                }
            }
        };
    }
}
