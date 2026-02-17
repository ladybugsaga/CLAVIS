package io.clavis.reactome;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.clavis.core.util.JsonUtils;

/**
 * Parses Reactome Content Service JSON responses into structured objects.
 */
public class ReactomeJsonParser {

    /**
     * Format a search result entry for MCP output.
     */
    public JsonObject formatSearchEntry(JsonObject entry) {
        JsonObject result = new JsonObject();
        result.addProperty("stId", JsonUtils.getString(entry, "stId", ""));
        result.addProperty("name", JsonUtils.getString(entry, "name", ""));
        result.addProperty("species", JsonUtils.getString(entry, "species", ""));
        result.addProperty("type", JsonUtils.getString(entry, "typeName", ""));
        result.addProperty("compartment", JsonUtils.getString(entry, "compartmentName", ""));
        result.addProperty("url", "https://reactome.org/content/detail/" + JsonUtils.getString(entry, "stId", ""));

        // Include summary text if available
        String summation = JsonUtils.getString(entry, "summation", "");
        if (!summation.isEmpty()) {
            result.addProperty("summary", summation);
        }
        return result;
    }

    /**
     * Format a pathway detail response for MCP output.
     */
    public JsonObject formatPathwayDetail(JsonObject pathway) {
        JsonObject result = new JsonObject();
        result.addProperty("dbId", JsonUtils.getInt(pathway, "dbId", 0));
        result.addProperty("stId", JsonUtils.getString(pathway, "stId", ""));
        result.addProperty("displayName", JsonUtils.getString(pathway, "displayName", ""));
        result.addProperty("speciesName", JsonUtils.getString(pathway, "speciesName", ""));
        result.addProperty("schemaClass", JsonUtils.getString(pathway, "schemaClass", ""));
        result.addProperty("isInDisease", pathway.has("isInDisease") && pathway.get("isInDisease").getAsBoolean());
        result.addProperty("hasDiagram", pathway.has("hasDiagram") && pathway.get("hasDiagram").getAsBoolean());
        result.addProperty("url", "https://reactome.org/content/detail/" + JsonUtils.getString(pathway, "stId", ""));

        // Extract summation text
        if (pathway.has("summation") && pathway.get("summation").isJsonArray()) {
            JsonArray summations = pathway.getAsJsonArray("summation");
            if (!summations.isEmpty()) {
                JsonObject firstSummation = summations.get(0).getAsJsonObject();
                result.addProperty("summary", JsonUtils.getString(firstSummation, "text", ""));
            }
        }

        // Extract sub-events (child pathways)
        if (pathway.has("hasEvent") && pathway.get("hasEvent").isJsonArray()) {
            JsonArray events = pathway.getAsJsonArray("hasEvent");
            JsonArray children = new JsonArray();
            for (JsonElement e : events) {
                JsonObject event = e.getAsJsonObject();
                JsonObject child = new JsonObject();
                child.addProperty("stId", JsonUtils.getString(event, "stId", ""));
                child.addProperty("displayName", JsonUtils.getString(event, "displayName", ""));
                child.addProperty("schemaClass", JsonUtils.getString(event, "schemaClass", ""));
                children.add(child);
            }
            result.add("childEvents", children);
        }

        // Extract literature references
        if (pathway.has("literatureReference") && pathway.get("literatureReference").isJsonArray()) {
            JsonArray refs = pathway.getAsJsonArray("literatureReference");
            JsonArray litRefs = new JsonArray();
            for (JsonElement e : refs) {
                JsonObject ref = e.getAsJsonObject();
                JsonObject litRef = new JsonObject();
                litRef.addProperty("title", JsonUtils.getString(ref, "title", ""));
                litRef.addProperty("journal", JsonUtils.getString(ref, "journal", ""));
                litRef.addProperty("year", JsonUtils.getInt(ref, "year", 0));
                int pmid = JsonUtils.getInt(ref, "pubMedIdentifier", 0);
                if (pmid > 0) {
                    litRef.addProperty("pubmed", "https://pubmed.ncbi.nlm.nih.gov/" + pmid);
                }
                litRefs.add(litRef);
            }
            result.add("references", litRefs);
        }

        return result;
    }

    /**
     * Format a participant entity for MCP output.
     */
    public JsonObject formatParticipant(JsonObject entity) {
        JsonObject result = new JsonObject();
        result.addProperty("dbId", JsonUtils.getInt(entity, "dbId", 0));
        result.addProperty("stId", JsonUtils.getString(entity, "stId", ""));
        result.addProperty("displayName", JsonUtils.getString(entity, "displayName", ""));
        result.addProperty("schemaClass", JsonUtils.getString(entity, "schemaClass", ""));
        return result;
    }

    /**
     * Format a simple pathway entry (for entity-to-pathway results).
     */
    public JsonObject formatSimplePathway(JsonObject pathway) {
        JsonObject result = new JsonObject();
        result.addProperty("stId", JsonUtils.getString(pathway, "stId", ""));
        result.addProperty("displayName", JsonUtils.getString(pathway, "displayName", ""));
        result.addProperty("speciesName", JsonUtils.getString(pathway, "speciesName", ""));
        result.addProperty("hasDiagram", pathway.has("hasDiagram") && pathway.get("hasDiagram").getAsBoolean());
        result.addProperty("url", "https://reactome.org/content/detail/" + JsonUtils.getString(pathway, "stId", ""));
        return result;
    }
}
