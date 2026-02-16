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

                JsonObject minYearProp = new JsonObject();
                minYearProp.addProperty("type", "string");
                minYearProp.addProperty("description", "Filter by minimum publication year (e.g., '2020')");
                properties.add("minYear", minYearProp);

                JsonObject maxYearProp = new JsonObject();
                maxYearProp.addProperty("type", "string");
                maxYearProp.addProperty("description", "Filter by maximum publication year (e.g., '2024')");
                properties.add("maxYear", maxYearProp);

                JsonObject articleTypeProp = new JsonObject();
                articleTypeProp.addProperty("type", "string");
                articleTypeProp.addProperty("description",
                        "Filter by article type (e.g., 'Review', 'Clinical Trial', 'Meta-Analysis')");
                articleTypeProp.add("enum", new JsonArray()); // Can populate enum if desired
                properties.add("articleType", articleTypeProp);

                JsonObject freeFullTextProp = new JsonObject();
                freeFullTextProp.addProperty("type", "boolean");
                freeFullTextProp.addProperty("description", "Filter for free full text only");
                properties.add("freeFullText", freeFullTextProp);

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
                            ? parameters.get("maxResults").getAsInt()
                            : 20;

                    StringBuilder finalQuery = new StringBuilder(query);
                    if (parameters.has("minYear") && parameters.has("maxYear")) {
                        finalQuery.append(" AND (")
                                .append(parameters.get("minYear").getAsString())
                                .append("[Date - Publication] : ")
                                .append(parameters.get("maxYear").getAsString())
                                .append("[Date - Publication])");
                    } else if (parameters.has("minYear")) {
                        finalQuery.append(" AND (")
                                .append(parameters.get("minYear").getAsString())
                                .append("[Date - Publication] : 3000[Date - Publication])");
                    }

                    if (parameters.has("articleType")) {
                        finalQuery.append(" AND ").append(parameters.get("articleType").getAsString())
                                .append("[Publication Type]");
                    }

                    if (parameters.has("freeFullText") && parameters.get("freeFullText").getAsBoolean()) {
                        finalQuery.append(" AND free full text[sb]");
                    }

                    List<Paper> papers = client.search(finalQuery.toString(), Math.min(maxResults, 100));
                    return formatPapersResponse(papers, finalQuery.toString());
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
                            ? parameters.get("maxResults").getAsInt()
                            : 10;

                    List<Paper> papers = client.getRelatedPapers(pmid, maxResults);
                    return formatPapersResponse(papers, "related:" + pmid);
                } catch (Exception e) {
                    throw new ToolExecutionException("Failed to get related papers: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the track_citations tool.
     */
    public MCPTool createTrackCitationsTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "track_citations";
            }

            @Override
            public String getDescription() {
                return "See who cites a paper and what it cites.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();
                JsonObject pmidProp = new JsonObject();
                pmidProp.addProperty("type", "string");
                pmidProp.addProperty("description", "PubMed ID");
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
                    var citations = client.getTrackedCitations(pmid);
                    JsonObject result = new JsonObject();
                    result.addProperty("pmid", pmid);
                    JsonArray citedBy = new JsonArray();
                    citations.get("cited_by").forEach(citedBy::add);
                    result.add("cited_by", citedBy);
                    JsonArray references = new JsonArray();
                    citations.get("references").forEach(references::add);
                    result.add("references", references);
                    return result;
                } catch (Exception e) {
                    throw new ToolExecutionException("Failed to track citations: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the batch_retrieve tool.
     */
    public MCPTool createBatchRetrieveTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "batch_retrieve";
            }

            @Override
            public String getDescription() {
                return "Retrieve details for multiple papers at once.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();
                JsonObject pmidsProp = new JsonObject();
                pmidsProp.addProperty("type", "string");
                pmidsProp.addProperty("description", "Comma-separated list of PMIDs");
                properties.add("pmids", pmidsProp);
                schema.add("properties", properties);
                JsonArray required = new JsonArray();
                required.add("pmids");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String pmidsStr = parameters.get("pmids").getAsString();
                    List<String> pmids = List.of(pmidsStr.split(","));
                    List<Paper> papers = client.batchRetrieve(pmids);
                    return formatPapersResponse(papers, "batch:" + pmidsStr);
                } catch (Exception e) {
                    throw new ToolExecutionException("Batch retrieve failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the search_by_author tool.
     */
    public MCPTool createSearchByAuthorTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "search_by_author";
            }

            @Override
            public String getDescription() {
                return "Find all papers by a specific researcher.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();
                JsonObject nameProp = new JsonObject();
                nameProp.addProperty("type", "string");
                nameProp.addProperty("description", "Author name (e.g., 'Watson JD')");
                properties.add("author", nameProp);
                schema.add("properties", properties);
                JsonArray required = new JsonArray();
                required.add("author");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String author = parameters.get("author").getAsString();
                    List<Paper> papers = client.search(author + "[Author]", 20);
                    return formatPapersResponse(papers, "author:" + author);
                } catch (Exception e) {
                    throw new ToolExecutionException("Author search failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the check_retractions tool.
     */
    public MCPTool createCheckRetractionsTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "check_retractions";
            }

            @Override
            public String getDescription() {
                return "Check if a paper has been retracted or corrected.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();
                JsonObject pmidProp = new JsonObject();
                pmidProp.addProperty("type", "string");
                pmidProp.addProperty("description", "PubMed ID");
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
                    String status = client.checkRetractions(pmid);
                    JsonObject result = new JsonObject();
                    result.addProperty("pmid", pmid);
                    result.addProperty("status", status);
                    return result;
                } catch (Exception e) {
                    throw new ToolExecutionException("Check retractions failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the get_related_database_links tool.
     */
    public MCPTool createRelatedDatabaseLinksTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "get_related_database_links";
            }

            @Override
            public String getDescription() {
                return "Get links to genes, proteins, clinical trials, etc.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();
                JsonObject pmidProp = new JsonObject();
                pmidProp.addProperty("type", "string");
                pmidProp.addProperty("description", "PubMed ID");
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
                    List<String> links = client.getRelatedDatabaseLinks(pmid);
                    JsonObject result = new JsonObject();
                    result.addProperty("pmid", pmid);
                    JsonArray linksArray = new JsonArray();
                    links.forEach(linksArray::add);
                    result.add("database_links", linksArray);
                    return result;
                } catch (Exception e) {
                    throw new ToolExecutionException("Get related database links failed: " + e.getMessage(), e);
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

        JsonArray meshArray = new JsonArray();
        for (String mesh : paper.getMeshTerms()) {
            meshArray.add(mesh);
        }
        obj.add("meshTerms", meshArray);

        JsonArray pubTypesArray = new JsonArray();
        for (String pt : paper.getPublicationTypes()) {
            pubTypesArray.add(pt);
        }
        obj.add("publicationTypes", pubTypesArray);

        return obj;
    }
}
