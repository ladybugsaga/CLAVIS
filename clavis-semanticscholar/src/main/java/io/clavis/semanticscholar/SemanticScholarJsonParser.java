package io.clavis.semanticscholar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.clavis.core.models.Author;
import io.clavis.core.models.Paper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parser for Semantic Scholar API JSON responses.
 *
 * <p>
 * Converts S2 JSON into unified {@link Paper} and {@link Author} models.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class SemanticScholarJsonParser {

    private SemanticScholarJsonParser() {
        // utility class
    }

    /**
     * Parses paper search results from the /paper/search endpoint.
     *
     * @param json JSON response
     * @return list of papers
     */
    public static List<Paper> parsePaperSearchResults(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("data")) {
                return Collections.emptyList();
            }
            JsonArray data = root.getAsJsonArray("data");
            return parsePaperArray(data);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Parses a single paper from the /paper/{id} endpoint.
     *
     * @param json JSON response
     * @return the paper, or null if parsing fails
     */
    public static Paper parseSinglePaper(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            return parsePaperObject(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses citation results from /paper/{id}/citations.
     * S2 wraps each citing paper in a {"citingPaper": {...}} object.
     *
     * @param json JSON response
     * @return list of citing papers
     */
    public static List<Paper> parseCitationResults(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("data")) {
                return Collections.emptyList();
            }
            JsonArray data = root.getAsJsonArray("data");
            List<Paper> papers = new ArrayList<>();
            for (JsonElement element : data) {
                JsonObject wrapper = element.getAsJsonObject();
                if (wrapper.has("citingPaper")) {
                    Paper paper = parsePaperObject(wrapper.getAsJsonObject("citingPaper"));
                    if (paper != null) {
                        papers.add(paper);
                    }
                }
            }
            return papers;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Parses reference results from /paper/{id}/references.
     * S2 wraps each cited paper in a {"citedPaper": {...}} object.
     *
     * @param json JSON response
     * @return list of referenced papers
     */
    public static List<Paper> parseReferenceResults(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("data")) {
                return Collections.emptyList();
            }
            JsonArray data = root.getAsJsonArray("data");
            List<Paper> papers = new ArrayList<>();
            for (JsonElement element : data) {
                JsonObject wrapper = element.getAsJsonObject();
                if (wrapper.has("citedPaper")) {
                    Paper paper = parsePaperObject(wrapper.getAsJsonObject("citedPaper"));
                    if (paper != null) {
                        papers.add(paper);
                    }
                }
            }
            return papers;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Parses papers from /author/{id}/papers.
     * S2 wraps each paper in a {"paper": {...}} object, but the
     * search endpoint returns them directly in "data".
     *
     * @param json JSON response
     * @return list of papers
     */
    public static List<Paper> parseAuthorPapersResults(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("data")) {
                return Collections.emptyList();
            }
            JsonArray data = root.getAsJsonArray("data");
            List<Paper> papers = new ArrayList<>();
            for (JsonElement element : data) {
                JsonObject item = element.getAsJsonObject();
                // Author papers endpoint may wrap in "paper" key or return directly
                JsonObject paperObj = item.has("paper") ? item.getAsJsonObject("paper") : item;
                Paper paper = parsePaperObject(paperObj);
                if (paper != null) {
                    papers.add(paper);
                }
            }
            return papers;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Parses recommendation results from /recommendations/v1/papers.
     *
     * @param json JSON response
     * @return list of recommended papers
     */
    public static List<Paper> parseRecommendationResults(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("recommendedPapers")) {
                return Collections.emptyList();
            }
            JsonArray data = root.getAsJsonArray("recommendedPapers");
            return parsePaperArray(data);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Parses author search results.
     *
     * @param json JSON response from /author/search
     * @return JSON object with parsed author data
     */
    public static JsonObject parseAuthorSearchResults(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject result = new JsonObject();
            result.addProperty("total", root.has("total") ? root.get("total").getAsInt() : 0);

            JsonArray authors = new JsonArray();
            if (root.has("data")) {
                for (JsonElement element : root.getAsJsonArray("data")) {
                    JsonObject authorObj = element.getAsJsonObject();
                    JsonObject author = new JsonObject();
                    author.addProperty("authorId", getStringOrNull(authorObj, "authorId"));
                    author.addProperty("name", getStringOrNull(authorObj, "name"));
                    author.addProperty("paperCount",
                            authorObj.has("paperCount") ? authorObj.get("paperCount").getAsInt() : 0);
                    author.addProperty("citationCount",
                            authorObj.has("citationCount") ? authorObj.get("citationCount").getAsInt() : 0);
                    author.addProperty("hIndex", authorObj.has("hIndex") ? authorObj.get("hIndex").getAsInt() : 0);
                    author.addProperty("url", getStringOrNull(authorObj, "url"));

                    if (authorObj.has("affiliations") && authorObj.get("affiliations").isJsonArray()) {
                        author.add("affiliations", authorObj.getAsJsonArray("affiliations"));
                    }
                    authors.add(author);
                }
            }
            result.add("authors", authors);
            return result;
        } catch (Exception e) {
            JsonObject empty = new JsonObject();
            empty.addProperty("total", 0);
            empty.add("authors", new JsonArray());
            return empty;
        }
    }

    /**
     * Parses a single author profile.
     *
     * @param json JSON response from /author/{id}
     * @return JSON object with author details
     */
    public static JsonObject parseAuthorDetails(String json) {
        try {
            JsonObject authorObj = JsonParser.parseString(json).getAsJsonObject();
            JsonObject result = new JsonObject();
            result.addProperty("authorId", getStringOrNull(authorObj, "authorId"));
            result.addProperty("name", getStringOrNull(authorObj, "name"));
            result.addProperty("paperCount", authorObj.has("paperCount") ? authorObj.get("paperCount").getAsInt() : 0);
            result.addProperty("citationCount",
                    authorObj.has("citationCount") ? authorObj.get("citationCount").getAsInt() : 0);
            result.addProperty("hIndex", authorObj.has("hIndex") ? authorObj.get("hIndex").getAsInt() : 0);
            result.addProperty("url", getStringOrNull(authorObj, "url"));

            if (authorObj.has("affiliations") && authorObj.get("affiliations").isJsonArray()) {
                result.add("affiliations", authorObj.getAsJsonArray("affiliations"));
            }
            return result;
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    // ---- Internal helpers ----

    private static List<Paper> parsePaperArray(JsonArray data) {
        List<Paper> papers = new ArrayList<>();
        for (JsonElement element : data) {
            Paper paper = parsePaperObject(element.getAsJsonObject());
            if (paper != null) {
                papers.add(paper);
            }
        }
        return papers;
    }

    private static Paper parsePaperObject(JsonObject obj) {
        try {
            String paperId = getStringOrNull(obj, "paperId");
            String title = getStringOrNull(obj, "title");
            if (paperId == null || title == null) {
                return null;
            }

            Paper.Builder builder = new Paper.Builder()
                    .id(paperId)
                    .source("semantic_scholar")
                    .title(title)
                    .abstractText(getStringOrNull(obj, "abstract"))
                    .citationCount(obj.has("citationCount") && !obj.get("citationCount").isJsonNull()
                            ? obj.get("citationCount").getAsInt()
                            : 0)
                    .url(getStringOrNull(obj, "url"));

            // Year → publicationDate
            if (obj.has("year") && !obj.get("year").isJsonNull()) {
                builder.publicationDate(String.valueOf(obj.get("year").getAsInt()));
            }

            // Journal
            if (obj.has("journal") && !obj.get("journal").isJsonNull()) {
                JsonObject journal = obj.getAsJsonObject("journal");
                builder.journal(getStringOrNull(journal, "name"));
            }

            // DOI from externalIds
            if (obj.has("externalIds") && !obj.get("externalIds").isJsonNull()) {
                JsonObject extIds = obj.getAsJsonObject("externalIds");
                if (extIds.has("DOI") && !extIds.get("DOI").isJsonNull()) {
                    builder.doi(extIds.get("DOI").getAsString());
                }
            }

            // Authors
            if (obj.has("authors") && obj.get("authors").isJsonArray()) {
                List<Author> authors = new ArrayList<>();
                for (JsonElement ae : obj.getAsJsonArray("authors")) {
                    JsonObject authorObj = ae.getAsJsonObject();
                    String name = getStringOrNull(authorObj, "name");
                    if (name != null) {
                        authors.add(new Author(name));
                    }
                }
                builder.authors(authors);
            }

            // Fields of study → keywords
            if (obj.has("fieldsOfStudy") && obj.get("fieldsOfStudy").isJsonArray()) {
                List<String> fields = new ArrayList<>();
                for (JsonElement fe : obj.getAsJsonArray("fieldsOfStudy")) {
                    if (!fe.isJsonNull()) {
                        fields.add(fe.getAsString());
                    }
                }
                builder.keywords(fields);
            }

            // Publication types
            if (obj.has("publicationTypes") && obj.get("publicationTypes").isJsonArray()) {
                List<String> types = new ArrayList<>();
                for (JsonElement te : obj.getAsJsonArray("publicationTypes")) {
                    if (!te.isJsonNull()) {
                        types.add(te.getAsString());
                    }
                }
                builder.publicationTypes(types);
            }

            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    private static String getStringOrNull(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }
}
