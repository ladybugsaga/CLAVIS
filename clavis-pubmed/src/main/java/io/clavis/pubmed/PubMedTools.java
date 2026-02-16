package io.clavis.pubmed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.mcp.ToolExecutionException;
import io.clavis.core.models.Author;
import io.clavis.core.models.Paper;

import java.util.List;

/**
 * MCP tool definitions for PubMed operations.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class PubMedTools {

    private final PubMedClient client;

    public PubMedTools(PubMedClient client) {
        this.client = client;
    }

    /**
     * Creates the search_pubmed tool.
     */
    public MCPTool createSearchTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "search_pubmed";
            }

            @Override
            public String getDescription() {
                return "Search PubMed for biomedical literature. Returns papers with titles, abstracts, authors, and DOIs.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");

                JsonObject properties = new JsonObject();

                JsonObject queryProp = new JsonObject();
                queryProp.addProperty("type", "string");
                queryProp.addProperty("description", "Search query (supports PubMed query syntax)");
                properties.add("query", queryProp);

                JsonObject maxResultsProp = new JsonObject();
                maxResultsProp.addProperty("type", "number");
                maxResultsProp.addProperty("description", "Maximum number of results to return (1-100)");
                maxResultsProp.addProperty("default", 20);
                properties.add("maxResults", maxResultsProp);

                schema.add("properties", properties);

                JsonArray required = new JsonArray();
                required.add("query");
                schema.add("required", required);

                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String query = parameters.get("query").getAsString();
                    int maxResults = parameters.has("maxResults")
                            ? parameters.get("maxResults").getAsInt() : 20;

                    List<Paper> papers = client.search(query, Math.min(maxResults, 100));
                    return formatPapersResponse(papers, query);
                } catch (Exception e) {
                    throw new ToolExecutionException("PubMed search failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the get_pubmed_paper tool.
     */
    public MCPTool createGetPaperTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "get_pubmed_paper";
            }

            @Override
            public String getDescription() {
                return "Retrieve a specific paper from PubMed by its PMID.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");

                JsonObject properties = new JsonObject();
                JsonObject pmidProp = new JsonObject();
                pmidProp.addProperty("type", "string");
                pmidProp.addProperty("description", "PubMed ID (PMID) of the paper");
                properties.add("pmid", pmidProp);
                schema.add("properties", properties);

                JsonArray required = new JsonArray();
                required.add("pmid");
                schema.add("required", required);

                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String pmid = parameters.get("pmid").getAsString();
                    Paper paper = client.fetchByPmid(pmid);

                    if (paper == null) {
                        JsonObject result = new JsonObject();
                        result.addProperty("error", "Paper not found: " + pmid);
                        return result;
                    }

                    return formatPaper(paper);
                } catch (Exception e) {
                    throw new ToolExecutionException("Failed to fetch paper: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the get_related_papers tool.
     */
    public MCPTool createRelatedPapersTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "get_related_papers";
            }

            @Override
            public String getDescription() {
                return "Find papers related to a given PubMed paper by PMID.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");

                JsonObject properties = new JsonObject();

                JsonObject pmidProp = new JsonObject();
                pmidProp.addProperty("type", "string");
                pmidProp.addProperty("description", "PubMed ID of the source paper");
                properties.add("pmid", pmidProp);

                JsonObject maxProp = new JsonObject();
                maxProp.addProperty("type", "number");
                maxProp.addProperty("description", "Maximum related papers to return");
                maxProp.addProperty("default", 10);
                properties.add("maxResults", maxProp);

                schema.add("properties", properties);

                JsonArray required = new JsonArray();
                required.add("pmid");
                schema.add("required", required);

                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String pmid = parameters.get("pmid").getAsString();
                    int maxResults = parameters.has("maxResults")
                            ? parameters.get("maxResults").getAsInt() : 10;

                    List<Paper> papers = client.getRelatedPapers(pmid, maxResults);
                    return formatPapersResponse(papers, "related:" + pmid);
                } catch (Exception e) {
                    throw new ToolExecutionException("Failed to get related papers: " + e.getMessage(), e);
                }
            }
        };
    }

    private JsonObject formatPapersResponse(List<Paper> papers, String query) {
        JsonObject result = new JsonObject();
        result.addProperty("query", query);
        result.addProperty("totalResults", papers.size());

        JsonArray papersArray = new JsonArray();
        for (Paper paper : papers) {
            papersArray.add(formatPaper(paper));
        }
        result.add("papers", papersArray);
        return result;
    }

    private JsonObject formatPaper(Paper paper) {
        JsonObject obj = new JsonObject();
        obj.addProperty("pmid", paper.getId());
        obj.addProperty("title", paper.getTitle());
        obj.addProperty("abstract", paper.getAbstractText());
        obj.addProperty("journal", paper.getJournal());
        obj.addProperty("publicationDate", paper.getPublicationDate());
        obj.addProperty("doi", paper.getDoi());
        obj.addProperty("url", paper.getUrl());

        JsonArray authorsArray = new JsonArray();
        for (Author author : paper.getAuthors()) {
            authorsArray.add(author.getName());
        }
        obj.add("authors", authorsArray);

        return obj;
    }
}
