package io.clavis.clinicaltrials;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Parses ClinicalTrials.gov API v2 JSON responses into simplified JSON.
 */
public class ClinicalTrialsJsonParser {

    /**
     * Parses the search results (list of studies).
     * API v2 returns: { "studies": [ { "protocolSection": { ... } } ],
     * "totalCount": N }
     */
    public static JsonObject parseStudySearch(String json) {
        JsonObject result = new JsonObject();
        JsonArray trials = new JsonArray();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            if (root.has("totalCount")) {
                result.addProperty("totalCount", root.get("totalCount").getAsInt());
            }

            if (root.has("studies")) {
                for (JsonElement el : root.getAsJsonArray("studies")) {
                    JsonObject study = el.getAsJsonObject();
                    trials.add(parseStudySummary(study));
                }
            }
            result.add("trials", trials);
            result.addProperty("returnedCount", trials.size());
        } catch (Exception e) {
            result.addProperty("error", "Failed to parse ClinicalTrials response: " + e.getMessage());
        }
        return result;
    }

    /**
     * Parses a single study detail response.
     */
    public static JsonObject parseStudyDetail(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            return parseStudyFull(root);
        } catch (Exception e) {
            JsonObject err = new JsonObject();
            err.addProperty("error", "Failed to parse study: " + e.getMessage());
            return err;
        }
    }

    /**
     * Extracts a summary from a study object.
     */
    private static JsonObject parseStudySummary(JsonObject study) {
        JsonObject summary = new JsonObject();

        JsonObject protocol = getNestedObject(study, "protocolSection");
        if (protocol == null)
            return summary;

        // Identification
        JsonObject idModule = getNestedObject(protocol, "identificationModule");
        if (idModule != null) {
            summary.addProperty("nctId", getStr(idModule, "nctId"));
            summary.addProperty("title", getStr(idModule, "briefTitle"));
            summary.addProperty("officialTitle", getStr(idModule, "officialTitle"));
        }

        // Status
        JsonObject statusModule = getNestedObject(protocol, "statusModule");
        if (statusModule != null) {
            summary.addProperty("status", getStr(statusModule, "overallStatus"));
            summary.addProperty("startDate", extractDate(statusModule, "startDateStruct"));
            summary.addProperty("completionDate", extractDate(statusModule, "primaryCompletionDateStruct"));
        }

        // Sponsor
        JsonObject sponsorModule = getNestedObject(protocol, "sponsorCollaboratorsModule");
        if (sponsorModule != null) {
            JsonObject leadSponsor = getNestedObject(sponsorModule, "leadSponsor");
            if (leadSponsor != null) {
                summary.addProperty("sponsor", getStr(leadSponsor, "name"));
            }
        }

        // Design
        JsonObject designModule = getNestedObject(protocol, "designModule");
        if (designModule != null) {
            summary.addProperty("studyType", getStr(designModule, "studyType"));
            JsonArray phases = getArray(designModule, "phases");
            if (phases != null && phases.size() > 0) {
                summary.addProperty("phase", phases.get(0).getAsString());
            }
            JsonObject enrollment = getNestedObject(designModule, "enrollmentInfo");
            if (enrollment != null) {
                summary.addProperty("enrollment", getStr(enrollment, "count"));
            }
        }

        // Conditions
        JsonObject condModule = getNestedObject(protocol, "conditionsModule");
        if (condModule != null) {
            summary.add("conditions", getArray(condModule, "conditions"));
        }

        // Interventions
        JsonObject armsModule = getNestedObject(protocol, "armsInterventionsModule");
        if (armsModule != null) {
            JsonArray interventions = getArray(armsModule, "interventions");
            if (interventions != null) {
                JsonArray names = new JsonArray();
                for (JsonElement el : interventions) {
                    JsonObject intv = el.getAsJsonObject();
                    String name = getStr(intv, "name");
                    String type = getStr(intv, "type");
                    names.add((type != null ? type + ": " : "") + (name != null ? name : ""));
                }
                summary.add("interventions", names);
            }
        }

        // Brief summary
        JsonObject descModule = getNestedObject(protocol, "descriptionModule");
        if (descModule != null) {
            String briefSummary = getStr(descModule, "briefSummary");
            if (briefSummary != null && briefSummary.length() > 500) {
                briefSummary = briefSummary.substring(0, 500) + "...";
            }
            summary.addProperty("briefSummary", briefSummary);
        }

        return summary;
    }

    /**
     * Full study parse — includes eligibility and contact info.
     */
    private static JsonObject parseStudyFull(JsonObject study) {
        JsonObject result = parseStudySummary(study);

        JsonObject protocol = getNestedObject(study, "protocolSection");
        if (protocol == null)
            return result;

        // Eligibility
        JsonObject eligModule = getNestedObject(protocol, "eligibilityModule");
        if (eligModule != null) {
            result.addProperty("eligibilityCriteria", getStr(eligModule, "eligibilityCriteria"));
            result.addProperty("sex", getStr(eligModule, "sex"));
            result.addProperty("minimumAge", getStr(eligModule, "minimumAge"));
            result.addProperty("maximumAge", getStr(eligModule, "maximumAge"));
        }

        // Contacts & Locations
        JsonObject contactModule = getNestedObject(protocol, "contactsLocationsModule");
        if (contactModule != null) {
            JsonArray locations = getArray(contactModule, "locations");
            if (locations != null) {
                JsonArray locs = new JsonArray();
                int maxLocs = Math.min(locations.size(), 10);
                for (int i = 0; i < maxLocs; i++) {
                    JsonObject loc = locations.get(i).getAsJsonObject();
                    JsonObject l = new JsonObject();
                    l.addProperty("facility", getStr(loc, "facility"));
                    l.addProperty("city", getStr(loc, "city"));
                    l.addProperty("state", getStr(loc, "state"));
                    l.addProperty("country", getStr(loc, "country"));
                    locs.add(l);
                }
                result.add("locations", locs);
            }
        }

        return result;
    }

    private static JsonObject getNestedObject(JsonObject obj, String key) {
        if (obj != null && obj.has(key) && obj.get(key).isJsonObject()) {
            return obj.getAsJsonObject(key);
        }
        return null;
    }

    private static JsonArray getArray(JsonObject obj, String key) {
        if (obj != null && obj.has(key) && obj.get(key).isJsonArray()) {
            return obj.getAsJsonArray(key);
        }
        return null;
    }

    private static String getStr(JsonObject obj, String key) {
        if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }

    private static String extractDate(JsonObject parent, String dateStructKey) {
        JsonObject dateStruct = getNestedObject(parent, dateStructKey);
        if (dateStruct != null) {
            return getStr(dateStruct, "date");
        }
        return null;
    }
}
