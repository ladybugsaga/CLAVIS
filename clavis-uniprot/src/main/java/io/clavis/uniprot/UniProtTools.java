package io.clavis.uniprot;

import com.google.gson.JsonObject;
import io.clavis.core.mcp.ToolExecutionException;
import io.clavis.core.mcp.MCPTool;

import java.util.ArrayList;
import java.util.List;

/**
 * MCP tool definitions for UniProt.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class UniProtTools {

    private final UniProtClient client;

    public UniProtTools(UniProtClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();
        tools.add(searchProteins());
        tools.add(getProtein());
        tools.add(getSequence());
        tools.add(searchByGene());
        tools.add(getProteinFunction());
        tools.add(searchByOrganism());
        return tools;
    }

    private MCPTool searchProteins() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "uniprot_search";
            }

            @Override
            public String getDescription() {
                return "Search UniProt's 250M+ protein database. Supports full-text search, organism filtering, and Swiss-Prot (reviewed) filtering. Returns protein accession, name, gene, organism, length, and function.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject props = new JsonObject();

                JsonObject query = new JsonObject();
                query.addProperty("type", "string");
                query.addProperty("description", "Search query (e.g. 'insulin', 'kinase AND organism_id:9606')");
                props.add("query", query);

                JsonObject maxResults = new JsonObject();
                maxResults.addProperty("type", "integer");
                maxResults.addProperty("description", "Maximum results to return (1-100, default 10)");
                props.add("maxResults", maxResults);

                JsonObject organism = new JsonObject();
                organism.addProperty("type", "string");
                organism.addProperty("description",
                        "Organism taxonomy ID filter (e.g. '9606' for human, '10090' for mouse)");
                props.add("organism", organism);

                JsonObject reviewed = new JsonObject();
                reviewed.addProperty("type", "boolean");
                reviewed.addProperty("description", "If true, only return reviewed (Swiss-Prot) entries");
                props.add("reviewed", reviewed);

                schema.add("properties", props);
                com.google.gson.JsonArray required = new com.google.gson.JsonArray();
                required.add("query");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) throws ToolExecutionException {
                try {
                    String query = params.get("query").getAsString();
                    int max = params.has("maxResults") ? params.get("maxResults").getAsInt() : 10;
                    String organism = params.has("organism") ? params.get("organism").getAsString() : null;
                    Boolean reviewed = params.has("reviewed") ? params.get("reviewed").getAsBoolean() : null;

                    String response = client.searchProteins(query, max, organism, reviewed);
                    JsonObject result = UniProtJsonParser.parseSearchResults(response);
                    result.addProperty("query", query);

                    JsonObject output = new JsonObject();
                    com.google.gson.JsonArray content = new com.google.gson.JsonArray();
                    JsonObject text = new JsonObject();
                    text.addProperty("type", "text");
                    text.addProperty("text", result.toString());
                    content.add(text);
                    output.add("content", content);
                    return output;
                } catch (Exception e) {
                    throw new ToolExecutionException("UniProt search failed: " + e.getMessage(), e);
                }
            }
        };
    }

    private MCPTool getProtein() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "uniprot_get_protein";
            }

            @Override
            public String getDescription() {
                return "Get detailed protein information from UniProt by accession ID. Returns protein name, genes, organism, sequence, function, subcellular location, disease associations, PDB structures, and key features (domains, active sites, binding sites).";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject props = new JsonObject();

                JsonObject accession = new JsonObject();
                accession.addProperty("type", "string");
                accession.addProperty("description",
                        "UniProt accession ID (e.g. 'P01308' for insulin, 'P38398' for BRCA1)");
                props.add("accession", accession);

                schema.add("properties", props);
                com.google.gson.JsonArray required = new com.google.gson.JsonArray();
                required.add("accession");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) throws ToolExecutionException {
                try {
                    String accession = params.get("accession").getAsString();
                    String response = client.getProtein(accession);
                    JsonObject result = UniProtJsonParser.parseProteinDetail(response);

                    JsonObject output = new JsonObject();
                    com.google.gson.JsonArray content = new com.google.gson.JsonArray();
                    JsonObject text = new JsonObject();
                    text.addProperty("type", "text");
                    text.addProperty("text", result.toString());
                    content.add(text);
                    output.add("content", content);
                    return output;
                } catch (Exception e) {
                    throw new ToolExecutionException("UniProt get protein failed: " + e.getMessage(), e);
                }
            }
        };
    }

    private MCPTool getSequence() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "uniprot_get_sequence";
            }

            @Override
            public String getDescription() {
                return "Get the FASTA amino acid sequence for a protein from UniProt.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject props = new JsonObject();

                JsonObject accession = new JsonObject();
                accession.addProperty("type", "string");
                accession.addProperty("description", "UniProt accession ID (e.g. 'P01308')");
                props.add("accession", accession);

                schema.add("properties", props);
                com.google.gson.JsonArray required = new com.google.gson.JsonArray();
                required.add("accession");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) throws ToolExecutionException {
                try {
                    String accession = params.get("accession").getAsString();
                    String fasta = client.getSequence(accession);

                    JsonObject output = new JsonObject();
                    com.google.gson.JsonArray content = new com.google.gson.JsonArray();
                    JsonObject text = new JsonObject();
                    text.addProperty("type", "text");
                    text.addProperty("text", fasta);
                    content.add(text);
                    output.add("content", content);
                    return output;
                } catch (Exception e) {
                    throw new ToolExecutionException("UniProt get sequence failed: " + e.getMessage(), e);
                }
            }
        };
    }

    private MCPTool searchByGene() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "uniprot_search_gene";
            }

            @Override
            public String getDescription() {
                return "Search UniProt for proteins by gene name (e.g. BRCA1, TP53, INS). Returns reviewed Swiss-Prot entries matching the gene.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject props = new JsonObject();

                JsonObject gene = new JsonObject();
                gene.addProperty("type", "string");
                gene.addProperty("description", "Gene name (e.g. 'BRCA1', 'TP53', 'INS', 'EGFR')");
                props.add("geneName", gene);

                JsonObject organism = new JsonObject();
                organism.addProperty("type", "string");
                organism.addProperty("description", "Optional organism taxonomy ID (e.g. '9606' for human)");
                props.add("organism", organism);

                JsonObject maxResults = new JsonObject();
                maxResults.addProperty("type", "integer");
                maxResults.addProperty("description", "Maximum results (default 10)");
                props.add("maxResults", maxResults);

                schema.add("properties", props);
                com.google.gson.JsonArray required = new com.google.gson.JsonArray();
                required.add("geneName");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) throws ToolExecutionException {
                try {
                    String geneName = params.get("geneName").getAsString();
                    String organism = params.has("organism") ? params.get("organism").getAsString() : null;
                    int max = params.has("maxResults") ? params.get("maxResults").getAsInt() : 10;

                    String response = client.searchByGene(geneName, organism, max);
                    JsonObject result = UniProtJsonParser.parseSearchResults(response);
                    result.addProperty("gene", geneName);

                    JsonObject output = new JsonObject();
                    com.google.gson.JsonArray content = new com.google.gson.JsonArray();
                    JsonObject text = new JsonObject();
                    text.addProperty("type", "text");
                    text.addProperty("text", result.toString());
                    content.add(text);
                    output.add("content", content);
                    return output;
                } catch (Exception e) {
                    throw new ToolExecutionException("UniProt gene search failed: " + e.getMessage(), e);
                }
            }
        };
    }

    private MCPTool getProteinFunction() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "uniprot_get_function";
            }

            @Override
            public String getDescription() {
                return "Get the functional annotation of a protein from UniProt. Returns function description, subcellular location, disease associations, and subunit information.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject props = new JsonObject();

                JsonObject accession = new JsonObject();
                accession.addProperty("type", "string");
                accession.addProperty("description", "UniProt accession ID");
                props.add("accession", accession);

                schema.add("properties", props);
                com.google.gson.JsonArray required = new com.google.gson.JsonArray();
                required.add("accession");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) throws ToolExecutionException {
                try {
                    String accession = params.get("accession").getAsString();
                    String response = client.getProteinFunction(accession);
                    JsonObject full = UniProtJsonParser.parseProteinDetail(response);

                    // Extract only functional info
                    JsonObject result = new JsonObject();
                    result.addProperty("accession", accession);
                    if (full.has("proteinName"))
                        result.addProperty("proteinName", full.get("proteinName").getAsString());
                    if (full.has("function"))
                        result.addProperty("function", full.get("function").getAsString());
                    if (full.has("subcellularLocation"))
                        result.addProperty("subcellularLocation", full.get("subcellularLocation").getAsString());
                    if (full.has("subunit"))
                        result.addProperty("subunit", full.get("subunit").getAsString());
                    if (full.has("diseases"))
                        result.add("diseases", full.getAsJsonArray("diseases"));

                    JsonObject output = new JsonObject();
                    com.google.gson.JsonArray content = new com.google.gson.JsonArray();
                    JsonObject text = new JsonObject();
                    text.addProperty("type", "text");
                    text.addProperty("text", result.toString());
                    content.add(text);
                    output.add("content", content);
                    return output;
                } catch (Exception e) {
                    throw new ToolExecutionException("UniProt get function failed: " + e.getMessage(), e);
                }
            }
        };
    }

    private MCPTool searchByOrganism() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "uniprot_search_organism";
            }

            @Override
            public String getDescription() {
                return "Search UniProt for proteins from a specific organism. Can optionally filter by keyword (e.g. 'kinase', 'receptor').";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject props = new JsonObject();

                JsonObject organism = new JsonObject();
                organism.addProperty("type", "string");
                organism.addProperty("description",
                        "Organism name (e.g. 'Homo sapiens', 'Escherichia coli', 'SARS-CoV-2')");
                props.add("organism", organism);

                JsonObject keyword = new JsonObject();
                keyword.addProperty("type", "string");
                keyword.addProperty("description",
                        "Optional keyword filter (e.g. 'kinase', 'receptor', 'transporter')");
                props.add("keyword", keyword);

                JsonObject maxResults = new JsonObject();
                maxResults.addProperty("type", "integer");
                maxResults.addProperty("description", "Maximum results (default 10)");
                props.add("maxResults", maxResults);

                schema.add("properties", props);
                com.google.gson.JsonArray required = new com.google.gson.JsonArray();
                required.add("organism");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) throws ToolExecutionException {
                try {
                    String organism = params.get("organism").getAsString();
                    String keyword = params.has("keyword") ? params.get("keyword").getAsString() : null;
                    int max = params.has("maxResults") ? params.get("maxResults").getAsInt() : 10;

                    String response = client.searchByOrganism(organism, keyword, max);
                    JsonObject result = UniProtJsonParser.parseSearchResults(response);
                    result.addProperty("organism", organism);

                    JsonObject output = new JsonObject();
                    com.google.gson.JsonArray content = new com.google.gson.JsonArray();
                    JsonObject text = new JsonObject();
                    text.addProperty("type", "text");
                    text.addProperty("text", result.toString());
                    content.add(text);
                    output.add("content", content);
                    return output;
                } catch (Exception e) {
                    throw new ToolExecutionException("UniProt organism search failed: " + e.getMessage(), e);
                }
            }
        };
    }
}
