package io.clavis.reactome;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.util.JsonUtils;

import java.io.IOException;

/**
 * MCP tools for Reactome pathway database.
 */
public class ReactomeTools {
    private final ReactomeClient client;
    private final ReactomeJsonParser parser;

    public ReactomeTools(ReactomeClient client) {
        this.client = client;
        this.parser = new ReactomeJsonParser();
    }

    public MCPTool createSearchTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "reactome_search";
            }

            @Override
            public String getDescription() {
                return "Search Reactome's 15K+ biological pathways, reactions, and entities. "
                        + "Covers signal transduction, metabolism, disease pathways, and more.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject query = new JsonObject();
                query.addProperty("type", "string");
                query.addProperty("description", "Search query (e.g. 'apoptosis', 'BRCA1', 'cell cycle')");
                properties.add("query", query);

                JsonObject species = new JsonObject();
                species.addProperty("type", "string");
                species.addProperty("description", "Species filter (default: 'Homo sapiens')");
                properties.add("species", species);

                JsonObject maxResults = new JsonObject();
                maxResults.addProperty("type", "integer");
                maxResults.addProperty("description", "Max results (default 10, max 30)");
                properties.add("maxResults", maxResults);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "query");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String query = params.get("query").getAsString();
                    String species = params.has("species") ? params.get("species").getAsString() : null;
                    int max = params.has("maxResults") ? Math.min(params.get("maxResults").getAsInt(), 30) : 10;

                    String json = client.search(query, species, max);
                    JsonObject response = com.google.gson.JsonParser.parseString(json).getAsJsonObject();

                    JsonObject result = new JsonObject();
                    if (response.has("results") && response.get("results").isJsonArray()) {
                        JsonArray allResults = response.getAsJsonArray("results");
                        JsonArray entries = new JsonArray();
                        int count = 0;
                        for (JsonElement group : allResults) {
                            JsonObject g = group.getAsJsonObject();
                            if (g.has("entries") && g.get("entries").isJsonArray()) {
                                for (JsonElement entry : g.getAsJsonArray("entries")) {
                                    if (count >= max)
                                        break;
                                    entries.add(parser.formatSearchEntry(entry.getAsJsonObject()));
                                    count++;
                                }
                            }
                            if (count >= max)
                                break;
                        }
                        result.addProperty("count", count);
                        result.add("entries", entries);
                    } else {
                        result.addProperty("count", 0);
                        result.add("entries", new JsonArray());
                    }
                    return result;
                } catch (IOException e) {
                    return JsonUtils.formatError("Reactome search failed: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createGetPathwayTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "reactome_get_pathway";
            }

            @Override
            public String getDescription() {
                return "Get detailed information about a Reactome pathway or reaction by its stable ID (e.g. R-HSA-1640170).";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject id = new JsonObject();
                id.addProperty("type", "string");
                id.addProperty("description", "Reactome stable ID (e.g. 'R-HSA-1640170')");
                properties.add("id", id);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "id");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String id = params.get("id").getAsString();
                    String json = client.getById(id);
                    JsonObject pathway = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                    return parser.formatPathwayDetail(pathway);
                } catch (IOException e) {
                    return JsonUtils.formatError("Failed to get pathway: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createGetParticipantsTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "reactome_get_participants";
            }

            @Override
            public String getDescription() {
                return "Get the molecular participants (proteins, compounds, complexes) involved in a Reactome pathway or reaction.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject id = new JsonObject();
                id.addProperty("type", "string");
                id.addProperty("description", "Reactome stable ID (e.g. 'R-HSA-141409')");
                properties.add("id", id);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "id");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String id = params.get("id").getAsString();
                    String json = client.getParticipants(id);

                    // Response is a JSON array of participants
                    JsonArray raw = com.google.gson.JsonParser.parseString(json).getAsJsonArray();
                    JsonObject result = new JsonObject();
                    result.addProperty("pathwayId", id);
                    result.addProperty("count", raw.size());
                    JsonArray participants = new JsonArray();
                    for (JsonElement e : raw) {
                        participants.add(parser.formatParticipant(e.getAsJsonObject()));
                    }
                    result.add("participants", participants);
                    return result;
                } catch (Exception e) {
                    return JsonUtils.formatError("Failed to get participants: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createGetPathwaysForEntityTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "reactome_get_pathways_for_entity";
            }

            @Override
            public String getDescription() {
                return "Find all Reactome pathways that contain a specific gene, protein, or compound. "
                        + "Accepts gene names (TP53), UniProt IDs (P04637), or ChEBI IDs.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject entity = new JsonObject();
                entity.addProperty("type", "string");
                entity.addProperty("description",
                        "Entity identifier: gene name (TP53), UniProt ID (P04637), or ChEBI ID");
                properties.add("entity", entity);

                JsonObject species = new JsonObject();
                species.addProperty("type", "string");
                species.addProperty("description", "Species (default: 'Homo sapiens')");
                properties.add("species", species);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "entity");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String entity = params.get("entity").getAsString();
                    String species = params.has("species") ? params.get("species").getAsString() : null;
                    String json = client.getPathwaysForEntity(entity, species);

                    JsonArray raw = com.google.gson.JsonParser.parseString(json).getAsJsonArray();
                    JsonObject result = new JsonObject();
                    result.addProperty("entity", entity);
                    result.addProperty("count", raw.size());
                    JsonArray pathways = new JsonArray();
                    for (JsonElement e : raw) {
                        pathways.add(parser.formatSimplePathway(e.getAsJsonObject()));
                    }
                    result.add("pathways", pathways);
                    return result;
                } catch (Exception e) {
                    return JsonUtils.formatError("Failed to find pathways for entity: " + e.getMessage());
                }
            }
        };
    }
}
