package io.clavis.uniprot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for UniProt REST API JSON responses.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class UniProtJsonParser {

    private UniProtJsonParser() {
    }

    /**
     * Parses search results from /uniprotkb/search.
     */
    public static JsonObject parseSearchResults(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject result = new JsonObject();

            JsonArray proteins = new JsonArray();
            if (root.has("results")) {
                for (JsonElement entry : root.getAsJsonArray("results")) {
                    proteins.add(parseProteinSummary(entry.getAsJsonObject()));
                }
            }

            result.addProperty("totalResults", proteins.size());
            result.add("proteins", proteins);
            return result;
        } catch (Exception e) {
            JsonObject empty = new JsonObject();
            empty.addProperty("totalResults", 0);
            empty.add("proteins", new JsonArray());
            return empty;
        }
    }

    /**
     * Parses a full protein entry from /uniprotkb/{accession}.
     */
    public static JsonObject parseProteinDetail(String json) {
        try {
            JsonObject entry = JsonParser.parseString(json).getAsJsonObject();
            JsonObject result = new JsonObject();

            result.addProperty("accession", getStr(entry, "primaryAccession"));
            result.addProperty("entryId", getStr(entry, "uniProtkbId"));
            result.addProperty("proteinName", extractProteinName(entry));

            // Genes
            if (entry.has("genes") && entry.get("genes").isJsonArray()) {
                JsonArray genes = new JsonArray();
                for (JsonElement g : entry.getAsJsonArray("genes")) {
                    JsonObject gene = g.getAsJsonObject();
                    if (gene.has("geneName")) {
                        genes.add(gene.getAsJsonObject("geneName").get("value").getAsString());
                    }
                }
                result.add("genes", genes);
            }

            // Organism
            if (entry.has("organism") && !entry.get("organism").isJsonNull()) {
                JsonObject org = entry.getAsJsonObject("organism");
                JsonObject organism = new JsonObject();
                organism.addProperty("scientificName", getStr(org, "scientificName"));
                organism.addProperty("commonName", getStr(org, "commonName"));
                organism.addProperty("taxonId", org.has("taxonId") ? org.get("taxonId").getAsInt() : 0);
                result.add("organism", organism);
            }

            // Sequence
            if (entry.has("sequence") && !entry.get("sequence").isJsonNull()) {
                JsonObject seq = entry.getAsJsonObject("sequence");
                JsonObject sequence = new JsonObject();
                sequence.addProperty("value", getStr(seq, "value"));
                sequence.addProperty("length", seq.has("length") ? seq.get("length").getAsInt() : 0);
                sequence.addProperty("molWeight", seq.has("molWeight") ? seq.get("molWeight").getAsInt() : 0);
                result.add("sequence", sequence);
            }

            // Function
            String function = extractComment(entry, "FUNCTION");
            if (function != null) {
                result.addProperty("function", function);
            }

            // Subcellular location
            String location = extractComment(entry, "SUBCELLULAR LOCATION");
            if (location != null) {
                result.addProperty("subcellularLocation", location);
            }

            // Subunit
            String subunit = extractComment(entry, "SUBUNIT");
            if (subunit != null) {
                result.addProperty("subunit", subunit);
            }

            // Disease involvement
            List<String> diseases = extractDiseases(entry);
            if (!diseases.isEmpty()) {
                JsonArray diseaseArray = new JsonArray();
                diseases.forEach(diseaseArray::add);
                result.add("diseases", diseaseArray);
            }

            // Cross-references (PDB structures)
            List<String> pdbIds = extractPdbIds(entry);
            if (!pdbIds.isEmpty()) {
                JsonArray pdbArray = new JsonArray();
                pdbIds.forEach(pdbArray::add);
                result.add("pdbStructures", pdbArray);
            }

            // Features (domains, active sites, etc.)
            JsonArray features = extractFeatures(entry);
            if (features.size() > 0) {
                result.add("features", features);
            }

            return result;
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    // ---- Internal helpers ----

    private static JsonObject parseProteinSummary(JsonObject entry) {
        JsonObject protein = new JsonObject();
        protein.addProperty("accession", getStr(entry, "primaryAccession"));
        protein.addProperty("entryId", getStr(entry, "uniProtkbId"));
        protein.addProperty("proteinName", extractProteinName(entry));

        // Genes
        if (entry.has("genes") && entry.get("genes").isJsonArray()) {
            JsonArray genes = new JsonArray();
            for (JsonElement g : entry.getAsJsonArray("genes")) {
                JsonObject gene = g.getAsJsonObject();
                if (gene.has("geneName")) {
                    genes.add(gene.getAsJsonObject("geneName").get("value").getAsString());
                }
            }
            protein.add("genes", genes);
        }

        // Organism
        if (entry.has("organism") && !entry.get("organism").isJsonNull()) {
            JsonObject org = entry.getAsJsonObject("organism");
            protein.addProperty("organism", getStr(org, "scientificName"));
        }

        // Length
        if (entry.has("sequence") && !entry.get("sequence").isJsonNull()) {
            JsonObject seq = entry.getAsJsonObject("sequence");
            protein.addProperty("length", seq.has("length") ? seq.get("length").getAsInt() : 0);
        }

        // Function
        String function = extractComment(entry, "FUNCTION");
        if (function != null) {
            protein.addProperty("function", function);
        }

        return protein;
    }

    private static String extractProteinName(JsonObject entry) {
        try {
            if (entry.has("proteinDescription")) {
                JsonObject desc = entry.getAsJsonObject("proteinDescription");
                if (desc.has("recommendedName")) {
                    JsonObject recName = desc.getAsJsonObject("recommendedName");
                    if (recName.has("fullName")) {
                        return recName.getAsJsonObject("fullName").get("value").getAsString();
                    }
                }
                if (desc.has("submittedName") && desc.get("submittedName").isJsonArray()) {
                    JsonArray submitted = desc.getAsJsonArray("submittedName");
                    if (submitted.size() > 0) {
                        JsonObject first = submitted.get(0).getAsJsonObject();
                        if (first.has("fullName")) {
                            return first.getAsJsonObject("fullName").get("value").getAsString();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String extractComment(JsonObject entry, String commentType) {
        try {
            if (entry.has("comments") && entry.get("comments").isJsonArray()) {
                for (JsonElement c : entry.getAsJsonArray("comments")) {
                    JsonObject comment = c.getAsJsonObject();
                    if (commentType.equals(getStr(comment, "commentType"))) {
                        if (comment.has("texts") && comment.get("texts").isJsonArray()) {
                            JsonArray texts = comment.getAsJsonArray("texts");
                            if (texts.size() > 0) {
                                return texts.get(0).getAsJsonObject().get("value").getAsString();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static List<String> extractDiseases(JsonObject entry) {
        List<String> diseases = new ArrayList<>();
        try {
            if (entry.has("comments") && entry.get("comments").isJsonArray()) {
                for (JsonElement c : entry.getAsJsonArray("comments")) {
                    JsonObject comment = c.getAsJsonObject();
                    if ("DISEASE".equals(getStr(comment, "commentType"))) {
                        if (comment.has("disease")) {
                            JsonObject disease = comment.getAsJsonObject("disease");
                            String name = getStr(disease, "diseaseId");
                            if (name != null) {
                                diseases.add(name);
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return diseases;
    }

    private static List<String> extractPdbIds(JsonObject entry) {
        List<String> pdbIds = new ArrayList<>();
        try {
            if (entry.has("uniProtKBCrossReferences") && entry.get("uniProtKBCrossReferences").isJsonArray()) {
                for (JsonElement ref : entry.getAsJsonArray("uniProtKBCrossReferences")) {
                    JsonObject xref = ref.getAsJsonObject();
                    if ("PDB".equals(getStr(xref, "database"))) {
                        String id = getStr(xref, "id");
                        if (id != null) {
                            pdbIds.add(id);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return pdbIds;
    }

    private static JsonArray extractFeatures(JsonObject entry) {
        JsonArray features = new JsonArray();
        try {
            if (entry.has("features") && entry.get("features").isJsonArray()) {
                for (JsonElement f : entry.getAsJsonArray("features")) {
                    JsonObject feat = f.getAsJsonObject();
                    String type = getStr(feat, "type");
                    if (type != null && (type.equals("Domain") || type.equals("Active site")
                            || type.equals("Binding site") || type.equals("Signal peptide")
                            || type.equals("Chain") || type.equals("Disulfide bond")
                            || type.equals("Modified residue"))) {
                        JsonObject feature = new JsonObject();
                        feature.addProperty("type", type);
                        feature.addProperty("description", getStr(feat, "description"));
                        if (feat.has("location")) {
                            JsonObject loc = feat.getAsJsonObject("location");
                            if (loc.has("start") && loc.has("end")) {
                                feature.addProperty("start", loc.getAsJsonObject("start").get("value").getAsInt());
                                feature.addProperty("end", loc.getAsJsonObject("end").get("value").getAsInt());
                            }
                        }
                        features.add(feature);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return features;
    }

    private static String getStr(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }
}
