package io.clavis.dbsnp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.util.JsonUtils;

import java.io.IOException;
import java.util.Map;

/**
 * MCP tools for NCBI dbSNP genetic variant database.
 */
public class DbSnpTools {
    private final DbSnpClient client;
    private final DbSnpJsonParser parser;

    public DbSnpTools(DbSnpClient client) {
        this.client = client;
        this.parser = new DbSnpJsonParser();
    }

    public MCPTool createGetVariantTool() {
        return new MCPTool() {
            @Override
            public String getName() { return "dbsnp_get_variant"; }

            @Override
            public String getDescription() {
                return "Get full details for a genetic variant by its rsID from NCBI dbSNP. "
                     + "Returns alleles, variant type, clinical significance, and population frequencies.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject rsId = new JsonObject();
                rsId.addProperty("type", "string");
                rsId.addProperty("description", "dbSNP rsID (e.g. 'rs7412', 'rs429358', or just '7412')");
                properties.add("rsId", rsId);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "rsId");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String rsId = params.get("rsId").getAsString();
                    String json = client.getRefSnp(rsId);
                    JsonObject refsnp = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                    return parser.formatRefSnp(refsnp);
                } catch (IOException e) {
                    return JsonUtils.formatError("Failed to get variant: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createSearchGeneTool() {
        return new MCPTool() {
            @Override
            public String getName() { return "dbsnp_search_gene"; }

            @Override
            public String getDescription() {
                return "Find genetic variants (SNPs) associated with a gene. "
                     + "Returns a list of rsIDs with summary information.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject gene = new JsonObject();
                gene.addProperty("type", "string");
                gene.addProperty("description", "Gene symbol (e.g. 'BRCA1', 'TP53', 'APOE')");
                properties.add("gene", gene);

                JsonObject maxResults = new JsonObject();
                maxResults.addProperty("type", "integer");
                maxResults.addProperty("description", "Max results (default 10, max 20)");
                properties.add("maxResults", maxResults);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "gene");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String gene = params.get("gene").getAsString();
                    int max = params.has("maxResults") ? Math.min(params.get("maxResults").getAsInt(), 20) : 10;

                    // Step 1: Search for SNP IDs
                    String searchJson = client.searchByGene(gene, max);
                    JsonObject searchResult = com.google.gson.JsonParser.parseString(searchJson).getAsJsonObject();

                    JsonObject result = new JsonObject();
                    result.addProperty("gene", gene);

                    if (searchResult.has("esearchresult")) {
                        JsonObject esr = searchResult.getAsJsonObject("esearchresult");
                        int count = Integer.parseInt(JsonUtils.getString(esr, "count", "0"));
                        result.addProperty("totalFound", count);

                        if (esr.has("idlist") && esr.get("idlist").isJsonArray()) {
                            JsonArray idList = esr.getAsJsonArray("idlist");
                            if (!idList.isEmpty()) {
                                // Step 2: Get summaries
                                StringBuilder ids = new StringBuilder();
                                for (JsonElement id : idList) {
                                    if (ids.length() > 0) ids.append(",");
                                    ids.append(id.getAsString());
                                }
                                String summaryJson = client.getSummary(ids.toString());
                                JsonObject summaryResult = com.google.gson.JsonParser
                                        .parseString(summaryJson).getAsJsonObject();

                                JsonArray variants = new JsonArray();
                                if (summaryResult.has("result")) {
                                    JsonObject resultObj = summaryResult.getAsJsonObject("result");
                                    for (JsonElement id : idList) {
                                        String snpId = id.getAsString();
                                        if (resultObj.has(snpId)) {
                                            variants.add(parser.formatSnpSummary(
                                                    snpId, resultObj.getAsJsonObject(snpId)));
                                        }
                                    }
                                }
                                result.addProperty("returnedCount", variants.size());
                                result.add("variants", variants);
                            } else {
                                result.addProperty("returnedCount", 0);
                                result.add("variants", new JsonArray());
                            }
                        }
                    }
                    return result;
                } catch (Exception e) {
                    return JsonUtils.formatError("Gene search failed: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createGetFrequencyTool() {
        return new MCPTool() {
            @Override
            public String getName() { return "dbsnp_get_frequency"; }

            @Override
            public String getDescription() {
                return "Get population allele frequency data for a genetic variant. "
                     + "Shows frequency across studies like GnomAD, 1000 Genomes, TOPMED, etc.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject rsId = new JsonObject();
                rsId.addProperty("type", "string");
                rsId.addProperty("description", "dbSNP rsID (e.g. 'rs7412')");
                properties.add("rsId", rsId);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "rsId");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String rsId = params.get("rsId").getAsString();
                    String json = client.getRefSnp(rsId);
                    JsonObject refsnp = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                    JsonObject full = parser.formatRefSnp(refsnp);

                    // Return only frequency-related data
                    JsonObject result = new JsonObject();
                    result.addProperty("rsId", full.get("rsId").getAsString());
                    result.addProperty("url", full.get("url").getAsString());
                    if (full.has("frequencies")) {
                        result.add("frequencies", full.get("frequencies"));
                    } else {
                        result.add("frequencies", new JsonArray());
                        result.addProperty("note", "No frequency data available for this variant");
                    }
                    return result;
                } catch (IOException e) {
                    return JsonUtils.formatError("Failed to get frequency data: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createGetClinicalTool() {
        return new MCPTool() {
            @Override
            public String getName() { return "dbsnp_get_clinical"; }

            @Override
            public String getDescription() {
                return "Get clinical significance and disease associations for a genetic variant. "
                     + "Includes ClinVar annotations and pathogenicity assessments.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject rsId = new JsonObject();
                rsId.addProperty("type", "string");
                rsId.addProperty("description", "dbSNP rsID (e.g. 'rs121913529')");
                properties.add("rsId", rsId);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "rsId");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String rsId = params.get("rsId").getAsString();
                    String json = client.getRefSnp(rsId);
                    JsonObject refsnp = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                    JsonObject full = parser.formatRefSnp(refsnp);

                    // Return only clinical data
                    JsonObject result = new JsonObject();
                    result.addProperty("rsId", full.get("rsId").getAsString());
                    result.addProperty("url", full.get("url").getAsString());
                    if (full.has("clinical")) {
                        result.add("clinical", full.get("clinical"));
                    } else {
                        result.add("clinical", new JsonArray());
                        result.addProperty("note", "No clinical data available for this variant");
                    }
                    return result;
                } catch (IOException e) {
                    return JsonUtils.formatError("Failed to get clinical data: " + e.getMessage());
                }
            }
        };
    }
}
