package io.clavis.europepmc;

import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.models.Paper;
import io.clavis.core.util.JsonUtils;

import java.io.IOException;
import java.util.List;

/**
 * MCP tools for Europe PMC.
 */
public class EuropePmcTools {
    private final EuropePmcClient client;

    public EuropePmcTools(EuropePmcClient client) {
        this.client = client;
    }

    public MCPTool createSearchTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "epmc_search";
            }

            @Override
            public String getDescription() {
                return "Search Europe PMC's collection of 40M+ biomedical papers, patents, and preprints.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject query = new JsonObject();
                query.addProperty("type", "string");
                query.addProperty("description", "Search query (e.g. 'cancer', 'author:\"Smith J\"')");
                properties.add("query", query);

                JsonObject pageSize = new JsonObject();
                pageSize.addProperty("type", "integer");
                pageSize.addProperty("description", "Number of results to return (default 10)");
                properties.add("pageSize", pageSize);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "query");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String query = params.get("query").getAsString();
                    int pageSize = params.has("pageSize") ? params.get("pageSize").getAsInt() : 10;
                    List<Paper> papers = client.search(query, pageSize);
                    return JsonUtils.formatPaperList(papers);
                } catch (IOException e) {
                    return JsonUtils.formatError("Europe PMC search failed: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createGetDetailsTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "epmc_get_details";
            }

            @Override
            public String getDescription() {
                return "Get full details for a specific Europe PMC article using ID and source.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject id = new JsonObject();
                id.addProperty("type", "string");
                id.addProperty("description", "Article ID (e.g. '33116279')");
                properties.add("id", id);

                JsonObject source = new JsonObject();
                source.addProperty("type", "string");
                source.addProperty("description",
                        "Data source: MED (PubMed), PMC (full text), PAT (patents), AGR (Agricola), etc. (default: MED)");
                properties.add("source", source);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "id");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String id = params.get("id").getAsString();
                    String source = params.has("source") ? params.get("source").getAsString() : "MED";
                    Paper paper = client.getDetails(id, source);
                    return paper != null ? JsonUtils.formatPaper(paper) : JsonUtils.formatError("Paper not found");
                } catch (IOException e) {
                    return JsonUtils.formatError("Failed to get details: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createGetCitationsTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "epmc_get_citations";
            }

            @Override
            public String getDescription() {
                return "Get list of articles that cite the specified Europe PMC article.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject id = new JsonObject();
                id.addProperty("type", "string");
                id.addProperty("description", "Article ID");
                properties.add("id", id);

                JsonObject source = new JsonObject();
                source.addProperty("type", "string");
                source.addProperty("description", "Data source (default MED)");
                properties.add("source", source);

                JsonObject pageSize = new JsonObject();
                pageSize.addProperty("type", "integer");
                properties.add("pageSize", pageSize);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "id");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String id = params.get("id").getAsString();
                    String source = params.has("source") ? params.get("source").getAsString() : "MED";
                    int pageSize = params.has("pageSize") ? params.get("pageSize").getAsInt() : 10;
                    List<Paper> papers = client.getCitations(id, source, pageSize);
                    return JsonUtils.formatPaperList(papers);
                } catch (IOException e) {
                    return JsonUtils.formatError("Failed to get citations: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createGetReferencesTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "epmc_get_references";
            }

            @Override
            public String getDescription() {
                return "Get literature references for the specified Europe PMC article.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject id = new JsonObject();
                id.addProperty("type", "string");
                properties.add("id", id);

                JsonObject source = new JsonObject();
                source.addProperty("type", "string");
                properties.add("source", source);

                JsonObject pageSize = new JsonObject();
                pageSize.addProperty("type", "integer");
                properties.add("pageSize", pageSize);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "id");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String id = params.get("id").getAsString();
                    String source = params.has("source") ? params.get("source").getAsString() : "MED";
                    int pageSize = params.has("pageSize") ? params.get("pageSize").getAsInt() : 10;
                    List<Paper> papers = client.getReferences(id, source, pageSize);
                    return JsonUtils.formatPaperList(papers);
                } catch (IOException e) {
                    return JsonUtils.formatError("Failed to get references: " + e.getMessage());
                }
            }
        };
    }
}
