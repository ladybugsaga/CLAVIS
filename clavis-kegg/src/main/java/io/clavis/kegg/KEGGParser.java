package io.clavis.kegg;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Converts KEGG plain-text responses into JSON.
 *
 * KEGG uses two main formats:
 * 1. Tab-delimited lists: "id\tdescription" (from find/list/link operations)
 * 2. Flat-file entries: indented key-value blocks (from get operation)
 */
public class KEGGParser {

    /**
     * Parses tab-delimited KEGG output (find, list, link).
     * Each line: "id\tdescription"
     */
    public static JsonObject parseTabDelimited(String text) {
        JsonObject result = new JsonObject();
        JsonArray entries = new JsonArray();

        if (text == null || text.isBlank()) {
            result.add("results", entries);
            result.addProperty("totalResults", 0);
            return result;
        }

        String[] lines = text.strip().split("\n");
        for (String line : lines) {
            if (line.isBlank())
                continue;
            String[] parts = line.split("\t", 2);
            JsonObject entry = new JsonObject();
            entry.addProperty("id", parts[0].strip());
            entry.addProperty("description", parts.length > 1 ? parts[1].strip() : "");
            entries.add(entry);
        }
        result.add("results", entries);
        result.addProperty("totalResults", entries.size());
        return result;
    }

    /**
     * Parses KEGG flat-file format (get operation).
     *
     * Format example:
     * ENTRY hsa00010 Pathway
     * NAME Glycolysis / Gluconeogenesis - Homo sapiens
     * DESCRIPTION ...
     * CLASS Metabolism; Carbohydrate metabolism
     * GENE ...
     * COMPOUND ...
     * ///
     *
     * Multi-line values are indented with spaces under the key.
     */
    public static JsonObject parseFlatFile(String text) {
        JsonObject result = new JsonObject();

        if (text == null || text.isBlank()) {
            result.addProperty("error", "Empty response");
            return result;
        }

        String[] lines = text.split("\n");
        String currentKey = null;
        StringBuilder currentValue = new StringBuilder();

        for (String line : lines) {
            if (line.equals("///"))
                break; // End of entry

            if (!line.isEmpty() && !Character.isWhitespace(line.charAt(0))) {
                // New key-value pair
                if (currentKey != null) {
                    addField(result, currentKey, currentValue.toString().strip());
                }
                int spaceIdx = findFirstSpace(line);
                if (spaceIdx > 0) {
                    currentKey = line.substring(0, spaceIdx).strip();
                    currentValue = new StringBuilder(line.substring(spaceIdx).strip());
                } else {
                    currentKey = line.strip();
                    currentValue = new StringBuilder();
                }
            } else if (currentKey != null) {
                // Continuation of previous key
                currentValue.append("\n").append(line.strip());
            }
        }

        // Save last field
        if (currentKey != null) {
            addField(result, currentKey, currentValue.toString().strip());
        }

        return result;
    }

    /**
     * Parses a KEGG flat-file field that contains sub-entries (like GENE or
     * COMPOUND).
     * Each sub-entry line: "id description"
     */
    public static JsonArray parseSubEntries(String value) {
        JsonArray arr = new JsonArray();
        if (value == null || value.isBlank())
            return arr;

        for (String line : value.split("\n")) {
            line = line.strip();
            if (line.isEmpty())
                continue;
            String[] parts = line.split("\\s+", 2);
            JsonObject entry = new JsonObject();
            entry.addProperty("id", parts[0]);
            if (parts.length > 1) {
                entry.addProperty("name", parts[1]);
            }
            arr.add(entry);
        }
        return arr;
    }

    private static void addField(JsonObject obj, String key, String value) {
        // Some fields contain sub-entries (GENE, COMPOUND, PATHWAY_MAP, etc.)
        // We keep them as strings; the tool handler can parse further if needed
        if (obj.has(key)) {
            // Append to existing value if key repeats
            String existing = obj.get(key).getAsString();
            obj.addProperty(key, existing + "\n" + value);
        } else {
            obj.addProperty(key, value);
        }
    }

    private static int findFirstSpace(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (Character.isWhitespace(line.charAt(i)))
                return i;
        }
        return -1;
    }
}
