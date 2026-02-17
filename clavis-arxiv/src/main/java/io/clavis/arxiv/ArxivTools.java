package io.clavis.arxiv;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.models.Author;
import io.clavis.core.models.Paper;
import io.clavis.core.util.JsonUtils;

import java.io.IOException;
import java.util.List;

/**
 * MCP tools for arXiv preprint search.
 */
public class ArxivTools {
    private final ArxivClient client;

    public ArxivTools(ArxivClient client) {
        this.client = client;
    }

    public MCPTool createSearchTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "arxiv_search";
            }

            @Override
            public String getDescription() {
                return "Search arXiv's 2.4M+ preprints across physics, math, CS, biology, and more. "
                        + "Supports field prefixes: ti: (title), au: (author), cat: (category), all: (any field).";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject query = new JsonObject();
                query.addProperty("type", "string");
                query.addProperty("description",
                        "Search query. Use prefixes like 'ti:transformer', 'au:Hinton', or plain text for all fields.");
                properties.add("query", query);

                JsonObject maxResults = new JsonObject();
                maxResults.addProperty("type", "integer");
                maxResults.addProperty("description", "Max results to return (default 10, max 50)");
                properties.add("maxResults", maxResults);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "query");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String query = params.get("query").getAsString();
                    int max = params.has("maxResults") ? Math.min(params.get("maxResults").getAsInt(), 50) : 10;
                    List<Paper> papers = client.search("all:" + query, max);
                    return formatArxivResults(papers);
                } catch (IOException e) {
                    return JsonUtils.formatError("arXiv search failed: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createGetPaperTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "arxiv_get_paper";
            }

            @Override
            public String getDescription() {
                return "Get full details for a specific arXiv paper by its ID (e.g. '2301.12345' or 'hep-ex/0307015').";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject id = new JsonObject();
                id.addProperty("type", "string");
                id.addProperty("description", "arXiv paper ID (e.g. '2301.12345')");
                properties.add("id", id);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "id");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String id = params.get("id").getAsString();
                    Paper paper = client.getById(id);
                    if (paper == null) {
                        return JsonUtils.formatError("Paper not found: " + id);
                    }
                    return formatArxivPaper(paper);
                } catch (IOException e) {
                    return JsonUtils.formatError("Failed to get paper: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createSearchAuthorTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "arxiv_search_author";
            }

            @Override
            public String getDescription() {
                return "Find arXiv papers by a specific author name.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject author = new JsonObject();
                author.addProperty("type", "string");
                author.addProperty("description", "Author name (e.g. 'Yann LeCun', 'Hinton')");
                properties.add("author", author);

                JsonObject maxResults = new JsonObject();
                maxResults.addProperty("type", "integer");
                maxResults.addProperty("description", "Max results (default 10)");
                properties.add("maxResults", maxResults);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "author");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String author = params.get("author").getAsString();
                    int max = params.has("maxResults") ? Math.min(params.get("maxResults").getAsInt(), 50) : 10;
                    List<Paper> papers = client.searchByAuthor(author, max);
                    return formatArxivResults(papers);
                } catch (IOException e) {
                    return JsonUtils.formatError("Author search failed: " + e.getMessage());
                }
            }
        };
    }

    public MCPTool createSearchCategoryTool() {
        return new MCPTool() {
            @Override
            public String getName() {
                return "arxiv_search_category";
            }

            @Override
            public String getDescription() {
                return "Browse arXiv papers by category (e.g. cs.AI, cs.LG, quant-ph, math.CO, q-bio.BM). "
                        + "Optionally filter by keyword within the category.";
            }

            @Override
            public JsonObject getInputSchema() {
                JsonObject schema = new JsonObject();
                schema.addProperty("type", "object");
                JsonObject properties = new JsonObject();

                JsonObject category = new JsonObject();
                category.addProperty("type", "string");
                category.addProperty("description",
                        "arXiv category (e.g. 'cs.AI', 'cs.LG', 'quant-ph', 'math.CO', 'q-bio.BM')");
                properties.add("category", category);

                JsonObject query = new JsonObject();
                query.addProperty("type", "string");
                query.addProperty("description", "Optional keyword filter within category");
                properties.add("query", query);

                JsonObject maxResults = new JsonObject();
                maxResults.addProperty("type", "integer");
                maxResults.addProperty("description", "Max results (default 10)");
                properties.add("maxResults", maxResults);

                schema.add("properties", properties);
                JsonUtils.addRequired(schema, "category");
                return schema;
            }

            @Override
            public JsonObject execute(JsonObject params) {
                try {
                    String category = params.get("category").getAsString();
                    String query = params.has("query") ? params.get("query").getAsString() : null;
                    int max = params.has("maxResults") ? Math.min(params.get("maxResults").getAsInt(), 50) : 10;
                    List<Paper> papers = client.searchByCategory(category, query, max);
                    return formatArxivResults(papers);
                } catch (IOException e) {
                    return JsonUtils.formatError("Category search failed: " + e.getMessage());
                }
            }
        };
    }

    // ---- Formatting helpers ----

    private JsonObject formatArxivResults(List<Paper> papers) {
        JsonObject result = new JsonObject();
        result.addProperty("count", papers.size());
        JsonArray array = new JsonArray();
        for (Paper paper : papers) {
            array.add(formatArxivPaper(paper));
        }
        result.add("papers", array);
        return result;
    }

    private JsonObject formatArxivPaper(Paper paper) {
        JsonObject obj = new JsonObject();
        obj.addProperty("arxivId", paper.getId());
        obj.addProperty("title", paper.getTitle());
        obj.addProperty("abstract", paper.getAbstractText());
        obj.addProperty("published", paper.getPublicationDate());
        obj.addProperty("doi", paper.getDoi());
        obj.addProperty("journal", paper.getJournal());
        obj.addProperty("url", paper.getUrl());
        obj.addProperty("pdfUrl", "https://arxiv.org/pdf/" + paper.getId());

        JsonArray authors = new JsonArray();
        for (Author a : paper.getAuthors()) {
            authors.add(a.getName());
        }
        obj.add("authors", authors);

        JsonArray categories = new JsonArray();
        for (String kw : paper.getKeywords()) {
            categories.add(kw);
        }
        obj.add("categories", categories);

        return obj;
    }
}
