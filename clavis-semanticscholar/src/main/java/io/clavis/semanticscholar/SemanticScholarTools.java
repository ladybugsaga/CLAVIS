package io.clavis.semanticscholar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.mcp.ToolExecutionException;
import io.clavis.core.models.Author;
import io.clavis.core.models.Paper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP tool definitions for Semantic Scholar operations.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class SemanticScholarTools {

    private final SemanticScholarClient client;

    public SemanticScholarTools(SemanticScholarClient client) {
        this.client = client;
    }

    /**
     * Creates the s2_search tool.
     */
    public MCPTool createSearchTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "s2_search";
            }

            @Override
            public String getDescription() {
                return "Search Semantic Scholar's 200M+ papers with filters for year, venue, and open access.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject queryProp = new JsonObject();
                queryProp.addProperty("type", "string");
                queryProp.addProperty("description", "Search query (e.g., 'machine learning for drug discovery')");
                properties.add("query", queryProp);

                JsonObject maxProp = new JsonObject();
                maxProp.addProperty("type", "integer");
                maxProp.addProperty("description", "Max results (1-100, default: 10)");
                properties.add("maxResults", maxProp);

                JsonObject yearProp = new JsonObject();
                yearProp.addProperty("type", "string");
                yearProp.addProperty("description", "Year filter: single year '2024' or range '2020-2024'");
                properties.add("year", yearProp);

                JsonObject venueProp = new JsonObject();
                venueProp.addProperty("type", "string");
                venueProp.addProperty("description", "Venue/journal filter (e.g., 'Nature', 'NeurIPS')");
                properties.add("venue", venueProp);

                JsonObject oaProp = new JsonObject();
                oaProp.addProperty("type", "boolean");
                oaProp.addProperty("description", "Only return open access papers");
                properties.add("openAccess", oaProp);

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
                            : 10;
                    String year = parameters.has("year")
                            ? parameters.get("year").getAsString()
                            : null;
                    Boolean openAccess = parameters.has("openAccess")
                            ? parameters.get("openAccess").getAsBoolean()
                            : null;
                    String venue = parameters.has("venue")
                            ? parameters.get("venue").getAsString()
                            : null;

                    List<Paper> papers = client.search(query, maxResults, year, openAccess, venue);
                    return formatPapersResponse(papers, query);
                } catch (Exception e) {
                    throw new ToolExecutionException("Search failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the s2_get_paper tool.
     */
    public MCPTool createGetPaperTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "s2_get_paper";
            }

            @Override
            public String getDescription() {
                return "Get paper details by Semantic Scholar ID, DOI, PMID, or ArXiv ID. Prefix external IDs: DOI:, PMID:, ArXiv:, CorpusId:.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();
                JsonObject idProp = new JsonObject();
                idProp.addProperty("type", "string");
                idProp.addProperty("description", "Paper ID (e.g., 'DOI:10.1038/s41586-021-03819-2' or S2 Paper ID)");
                properties.add("paperId", idProp);
                schema.add("properties", properties);
                JsonArray required = new JsonArray();
                required.add("paperId");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String paperId = parameters.get("paperId").getAsString();
                    Paper paper = client.getPaper(paperId);
                    if (paper == null) {
                        throw new ToolExecutionException("Paper not found: " + paperId);
                    }
                    return formatPaper(paper);
                } catch (ToolExecutionException e) {
                    throw e;
                } catch (Exception e) {
                    throw new ToolExecutionException("Get paper failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the s2_get_citations tool.
     */
    public MCPTool createGetCitationsTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "s2_get_citations";
            }

            @Override
            public String getDescription() {
                return "Get papers that cite a given paper (forward citations).";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject idProp = new JsonObject();
                idProp.addProperty("type", "string");
                idProp.addProperty("description", "Paper ID");
                properties.add("paperId", idProp);

                JsonObject maxProp = new JsonObject();
                maxProp.addProperty("type", "integer");
                maxProp.addProperty("description", "Max results (default: 20)");
                properties.add("maxResults", maxProp);

                schema.add("properties", properties);
                JsonArray required = new JsonArray();
                required.add("paperId");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String paperId = parameters.get("paperId").getAsString();
                    int maxResults = parameters.has("maxResults")
                            ? parameters.get("maxResults").getAsInt()
                            : 20;
                    List<Paper> papers = client.getCitations(paperId, maxResults);
                    return formatPapersResponse(papers, "citations:" + paperId);
                } catch (Exception e) {
                    throw new ToolExecutionException("Get citations failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the s2_get_references tool.
     */
    public MCPTool createGetReferencesTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "s2_get_references";
            }

            @Override
            public String getDescription() {
                return "Get papers referenced by a given paper (backward citations).";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject idProp = new JsonObject();
                idProp.addProperty("type", "string");
                idProp.addProperty("description", "Paper ID");
                properties.add("paperId", idProp);

                JsonObject maxProp = new JsonObject();
                maxProp.addProperty("type", "integer");
                maxProp.addProperty("description", "Max results (default: 20)");
                properties.add("maxResults", maxProp);

                schema.add("properties", properties);
                JsonArray required = new JsonArray();
                required.add("paperId");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String paperId = parameters.get("paperId").getAsString();
                    int maxResults = parameters.has("maxResults")
                            ? parameters.get("maxResults").getAsInt()
                            : 20;
                    List<Paper> papers = client.getReferences(paperId, maxResults);
                    return formatPapersResponse(papers, "references:" + paperId);
                } catch (Exception e) {
                    throw new ToolExecutionException("Get references failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the s2_search_author tool.
     */
    public MCPTool createSearchAuthorTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "s2_search_author";
            }

            @Override
            public String getDescription() {
                return "Search for authors by name on Semantic Scholar.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject nameProp = new JsonObject();
                nameProp.addProperty("type", "string");
                nameProp.addProperty("description", "Author name to search for");
                properties.add("name", nameProp);

                JsonObject maxProp = new JsonObject();
                maxProp.addProperty("type", "integer");
                maxProp.addProperty("description", "Max results (default: 10)");
                properties.add("maxResults", maxProp);

                schema.add("properties", properties);
                JsonArray required = new JsonArray();
                required.add("name");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String name = parameters.get("name").getAsString();
                    int maxResults = parameters.has("maxResults")
                            ? parameters.get("maxResults").getAsInt()
                            : 10;
                    String json = client.searchAuthors(name, maxResults);
                    return SemanticScholarJsonParser.parseAuthorSearchResults(json);
                } catch (Exception e) {
                    throw new ToolExecutionException("Author search failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the s2_get_author tool.
     */
    public MCPTool createGetAuthorTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "s2_get_author";
            }

            @Override
            public String getDescription() {
                return "Get an author's profile including h-index, citation count, and affiliations.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();
                JsonObject idProp = new JsonObject();
                idProp.addProperty("type", "string");
                idProp.addProperty("description", "Semantic Scholar Author ID");
                properties.add("authorId", idProp);
                schema.add("properties", properties);
                JsonArray required = new JsonArray();
                required.add("authorId");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String authorId = parameters.get("authorId").getAsString();
                    String json = client.getAuthor(authorId);
                    return SemanticScholarJsonParser.parseAuthorDetails(json);
                } catch (Exception e) {
                    throw new ToolExecutionException("Get author failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the s2_get_author_papers tool.
     */
    public MCPTool createGetAuthorPapersTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "s2_get_author_papers";
            }

            @Override
            public String getDescription() {
                return "Get all papers by a specific author using their Semantic Scholar Author ID.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject idProp = new JsonObject();
                idProp.addProperty("type", "string");
                idProp.addProperty("description", "Semantic Scholar Author ID");
                properties.add("authorId", idProp);

                JsonObject maxProp = new JsonObject();
                maxProp.addProperty("type", "integer");
                maxProp.addProperty("description", "Max results (default: 20)");
                properties.add("maxResults", maxProp);

                schema.add("properties", properties);
                JsonArray required = new JsonArray();
                required.add("authorId");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String authorId = parameters.get("authorId").getAsString();
                    int maxResults = parameters.has("maxResults")
                            ? parameters.get("maxResults").getAsInt()
                            : 20;
                    List<Paper> papers = client.getAuthorPapers(authorId, maxResults);
                    return formatPapersResponse(papers, "author:" + authorId);
                } catch (Exception e) {
                    throw new ToolExecutionException("Get author papers failed: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Creates the s2_recommend_papers tool.
     */
    public MCPTool createRecommendPapersTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "s2_recommend_papers";
            }

            @Override
            public String getDescription() {
                return "Get AI-powered paper recommendations based on seed papers. Provide one or more paper IDs as positive examples.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject seedProp = new JsonObject();
                seedProp.addProperty("type", "string");
                seedProp.addProperty("description", "Comma-separated list of S2 Paper IDs to use as positive seeds");
                properties.add("paperIds", seedProp);

                JsonObject maxProp = new JsonObject();
                maxProp.addProperty("type", "integer");
                maxProp.addProperty("description", "Max recommendations (default: 10, max: 500)");
                properties.add("maxResults", maxProp);

                schema.add("properties", properties);
                JsonArray required = new JsonArray();
                required.add("paperIds");
                schema.add("required", required);
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
                try {
                    String paperIdsStr = parameters.get("paperIds").getAsString();
                    List<String> paperIds = Arrays.stream(paperIdsStr.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());

                    int maxResults = parameters.has("maxResults")
                            ? parameters.get("maxResults").getAsInt()
                            : 10;

                    List<Paper> papers = client.getRecommendations(paperIds, maxResults);
                    return formatPapersResponse(papers, "recommendations");
                } catch (Exception e) {
                    throw new ToolExecutionException("Recommendations failed: " + e.getMessage(), e);
                }
            }
        };
    }

    // ---- Formatting helpers ----

    private JsonObject formatPapersResponse(List<Paper> papers, String query) {
        JsonObject response = new JsonObject();
        response.addProperty("query", query);
        response.addProperty("totalResults", papers.size());

        JsonArray papersArray = new JsonArray();
        for (Paper paper : papers) {
            papersArray.add(formatPaper(paper));
        }
        response.add("papers", papersArray);
        return response;
    }

    private JsonObject formatPaper(Paper paper) {
        JsonObject obj = new JsonObject();
        obj.addProperty("paperId", paper.getId());
        obj.addProperty("title", paper.getTitle());
        obj.addProperty("abstract", paper.getAbstractText());
        obj.addProperty("journal", paper.getJournal());
        obj.addProperty("year", paper.getPublicationDate());
        obj.addProperty("doi", paper.getDoi());
        obj.addProperty("citationCount", paper.getCitationCount());
        obj.addProperty("url", paper.getUrl());

        JsonArray authorsArray = new JsonArray();
        if (paper.getAuthors() != null) {
            for (Author author : paper.getAuthors()) {
                authorsArray.add(author.getName());
            }
        }
        obj.add("authors", authorsArray);

        if (paper.getKeywords() != null && !paper.getKeywords().isEmpty()) {
            JsonArray fieldsArray = new JsonArray();
            paper.getKeywords().forEach(fieldsArray::add);
            obj.add("fieldsOfStudy", fieldsArray);
        }

        if (paper.getPublicationTypes() != null && !paper.getPublicationTypes().isEmpty()) {
            JsonArray typesArray = new JsonArray();
            paper.getPublicationTypes().forEach(typesArray::add);
            obj.add("publicationTypes", typesArray);
        }

        return obj;
    }
}
