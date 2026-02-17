package io.clavis.pubchem;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Parses PubChem PUG REST JSON responses into simplified JSON.
 */
public class PubChemJsonParser {

    /**
     * Parses property search results.
     * PUG REST returns: { "PropertyTable": { "Properties": [ { ... } ] } }
     */
    public static JsonObject parsePropertySearch(String json) {
        JsonObject result = new JsonObject();
        JsonArray compounds = new JsonArray();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("PropertyTable")) {
                JsonObject table = root.getAsJsonObject("PropertyTable");
                if (table.has("Properties")) {
                    for (JsonElement el : table.getAsJsonArray("Properties")) {
                        JsonObject prop = el.getAsJsonObject();
                        JsonObject compound = new JsonObject();
                        copyIfPresent(prop, compound, "CID");
                        copyIfPresent(prop, compound, "MolecularFormula");
                        copyIfPresent(prop, compound, "MolecularWeight");
                        copyIfPresent(prop, compound, "IUPACName");
                        copyIfPresent(prop, compound, "CanonicalSMILES");
                        copyIfPresent(prop, compound, "IsomericSMILES");
                        copyIfPresent(prop, compound, "InChIKey");
                        copyIfPresent(prop, compound, "XLogP");
                        copyIfPresent(prop, compound, "HBondDonorCount");
                        copyIfPresent(prop, compound, "HBondAcceptorCount");
                        copyIfPresent(prop, compound, "ExactMass");
                        copyIfPresent(prop, compound, "TPSA");
                        copyIfPresent(prop, compound, "Complexity");
                        copyIfPresent(prop, compound, "Charge");
                        compounds.add(compound);
                    }
                }
            }
            result.add("compounds", compounds);
            result.addProperty("totalResults", compounds.size());
        } catch (Exception e) {
            result.addProperty("error", "Failed to parse PubChem response: " + e.getMessage());
        }
        return result;
    }

    /**
     * Parses compound description response.
     * Returns: { "InformationList": { "Information": [ { "Title": ...,
     * "Description": ... } ] } }
     */
    public static JsonObject parseDescription(String json) {
        JsonObject result = new JsonObject();
        JsonArray descriptions = new JsonArray();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("InformationList")) {
                JsonObject infoList = root.getAsJsonObject("InformationList");
                if (infoList.has("Information")) {
                    for (JsonElement el : infoList.getAsJsonArray("Information")) {
                        JsonObject info = el.getAsJsonObject();
                        JsonObject desc = new JsonObject();
                        copyIfPresent(info, desc, "CID");
                        copyIfPresent(info, desc, "Title");
                        copyIfPresent(info, desc, "Description");
                        copyIfPresent(info, desc, "DescriptionSourceName");
                        copyIfPresent(info, desc, "DescriptionURL");
                        descriptions.add(desc);
                    }
                }
            }
            result.add("descriptions", descriptions);
            result.addProperty("totalResults", descriptions.size());
        } catch (Exception e) {
            result.addProperty("error", "Failed to parse PubChem description: " + e.getMessage());
        }
        return result;
    }

    /**
     * Parses synonyms response.
     * Returns: { "InformationList": { "Information": [ { "CID": N, "Synonym": [...]
     * } ] } }
     */
    public static JsonObject parseSynonyms(String json) {
        JsonObject result = new JsonObject();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("InformationList")) {
                JsonObject infoList = root.getAsJsonObject("InformationList");
                if (infoList.has("Information")) {
                    JsonArray infoArray = infoList.getAsJsonArray("Information");
                    if (infoArray.size() > 0) {
                        JsonObject first = infoArray.get(0).getAsJsonObject();
                        copyIfPresent(first, result, "CID");
                        if (first.has("Synonym")) {
                            JsonArray allSynonyms = first.getAsJsonArray("Synonym");
                            // Limit to first 20 synonyms
                            JsonArray limited = new JsonArray();
                            int max = Math.min(allSynonyms.size(), 20);
                            for (int i = 0; i < max; i++) {
                                limited.add(allSynonyms.get(i));
                            }
                            result.add("synonyms", limited);
                            result.addProperty("totalSynonyms", allSynonyms.size());
                        }
                    }
                }
            }
        } catch (Exception e) {
            result.addProperty("error", "Failed to parse PubChem synonyms: " + e.getMessage());
        }
        return result;
    }

    private static void copyIfPresent(JsonObject source, JsonObject target, String key) {
        if (source.has(key) && !source.get(key).isJsonNull()) {
            target.add(key, source.get(key));
        }
    }
}
