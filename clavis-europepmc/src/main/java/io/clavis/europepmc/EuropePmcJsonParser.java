package io.clavis.europepmc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.clavis.core.models.Author;
import io.clavis.core.models.Paper;
import io.clavis.core.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Europe PMC JSON responses.
 */
public class EuropePmcJsonParser {

    /**
     * Parses the search response into a list of papers.
     *
     * @param json response string
     * @return list of papers
     */
    public List<Paper> parseSearchResults(String json) {
        List<Paper> papers = new ArrayList<>();
        JsonObject root = JsonUtils.parseObject(json);
        if (root == null || !root.has("resultList"))
            return papers;

        JsonObject resultList = root.getAsJsonObject("resultList");
        if (!resultList.has("result"))
            return papers;

        JsonArray results = resultList.getAsJsonArray("result");
        for (JsonElement element : results) {
            if (element.isJsonObject()) {
                papers.add(parsePaper(element.getAsJsonObject()));
            }
        }
        return papers;
    }

    /**
     * Parses a single paper entry.
     *
     * @param result JsonObject
     * @return Paper model
     */
    public Paper parsePaper(JsonObject result) {
        Paper.Builder builder = new Paper.Builder()
                .id(JsonUtils.getString(result, "id", ""))
                .source(JsonUtils.getString(result, "source", "MED"))
                .title(JsonUtils.getString(result, "title", ""))
                .journal(JsonUtils.getString(result, "journalTitle", ""))
                .publicationDate(JsonUtils.getString(result, "pubYear", ""))
                .doi(JsonUtils.getString(result, "doi", ""))
                .abstractText(JsonUtils.getString(result, "abstractText", ""));

        String authorString = JsonUtils.getString(result, "authorString", "");
        if (!authorString.isEmpty()) {
            for (String name : authorString.split(", ")) {
                builder.addAuthor(new Author(name));
            }
        }

        // Add keywords
        if (result.has("keywordList")) {
            JsonObject keywordList = result.getAsJsonObject("keywordList");
            if (keywordList.has("keyword")) {
                JsonArray keywords = keywordList.getAsJsonArray("keyword");
                for (JsonElement k : keywords) {
                    builder.keywords(List.of(k.getAsString()));
                }
            }
        }

        // Add MeSH terms
        if (result.has("meshHeadingList")) {
            JsonObject meshList = result.getAsJsonObject("meshHeadingList");
            if (meshList.has("meshHeading")) {
                JsonArray terms = meshList.getAsJsonArray("meshHeading");
                for (JsonElement t : terms) {
                    if (t.isJsonObject()) {
                        builder.meshTerms(List.of(JsonUtils.getString(t.getAsJsonObject(), "descriptorName", "")));
                    }
                }
            }
        }

        // Add URL
        String id = JsonUtils.getString(result, "id", "");
        if (!id.isEmpty()) {
            builder.url("https://europepmc.org/article/" + JsonUtils.getString(result, "source", "MED") + "/" + id);
        }

        return builder.build();
    }
}
