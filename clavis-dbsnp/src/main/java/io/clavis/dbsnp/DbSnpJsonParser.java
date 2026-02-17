package io.clavis.dbsnp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.clavis.core.util.JsonUtils;

/**
 * Parses NCBI Variation Services API and E-utilities JSON responses.
 */
public class DbSnpJsonParser {

    /**
     * Format a RefSNP response from the Variation API.
     */
    public JsonObject formatRefSnp(JsonObject refsnp) {
        JsonObject result = new JsonObject();

        String rsId = JsonUtils.getString(refsnp, "refsnp_id", "");
        result.addProperty("rsId", "rs" + rsId);
        result.addProperty("url", "https://www.ncbi.nlm.nih.gov/snp/rs" + rsId);

        // Extract primary snapshot data
        if (refsnp.has("primary_snapshot_data")) {
            JsonObject snapshot = refsnp.getAsJsonObject("primary_snapshot_data");

            // Variant type
            result.addProperty("variantType", JsonUtils.getString(snapshot, "variant_type", ""));

            // Allele annotations — extract from placements_with_allele
            if (snapshot.has("placements_with_allele")) {
                JsonArray placements = snapshot.getAsJsonArray("placements_with_allele");
                JsonArray alleles = new JsonArray();
                for (JsonElement p : placements) {
                    JsonObject placement = p.getAsJsonObject();
                    if (placement.has("alleles")) {
                        for (JsonElement a : placement.getAsJsonArray("alleles")) {
                            JsonObject allele = a.getAsJsonObject();
                            if (allele.has("allele") && allele.getAsJsonObject("allele").has("spdi")) {
                                JsonObject spdi = allele.getAsJsonObject("allele").getAsJsonObject("spdi");
                                JsonObject formatted = new JsonObject();
                                formatted.addProperty("deletedSequence",
                                        JsonUtils.getString(spdi, "deleted_sequence", ""));
                                formatted.addProperty("insertedSequence",
                                        JsonUtils.getString(spdi, "inserted_sequence", ""));
                                formatted.addProperty("position", JsonUtils.getInt(spdi, "position", 0));
                                alleles.add(formatted);
                            }
                        }
                        break; // Only first placement
                    }
                }
                result.add("alleles", alleles);
            }

            // Allele annotations — extract clinical significance and frequency
            if (snapshot.has("allele_annotations")) {
                JsonArray annotations = snapshot.getAsJsonArray("allele_annotations");
                JsonArray clinicalEntries = new JsonArray();
                JsonArray frequencyEntries = new JsonArray();

                for (JsonElement ann : annotations) {
                    JsonObject annotation = ann.getAsJsonObject();

                    // Clinical data
                    if (annotation.has("clinical")) {
                        for (JsonElement c : annotation.getAsJsonArray("clinical")) {
                            JsonObject clin = c.getAsJsonObject();
                            JsonObject clinEntry = new JsonObject();
                            if (clin.has("clinical_significances")) {
                                clinEntry.add("significances", clin.getAsJsonArray("clinical_significances"));
                            }
                            if (clin.has("disease_names")) {
                                clinEntry.add("diseases", clin.getAsJsonArray("disease_names"));
                            }
                            if (clin.has("accession_version")) {
                                clinEntry.addProperty("clinVarAccession",
                                        clin.get("accession_version").getAsString());
                            }
                            clinicalEntries.add(clinEntry);
                        }
                    }

                    // Frequency data
                    if (annotation.has("frequency")) {
                        for (JsonElement f : annotation.getAsJsonArray("frequency")) {
                            JsonObject freq = f.getAsJsonObject();
                            JsonObject freqEntry = new JsonObject();
                            freqEntry.addProperty("study",
                                    JsonUtils.getString(freq, "study_name", ""));
                            freqEntry.addProperty("alleleCount",
                                    JsonUtils.getInt(freq, "allele_count", 0));
                            freqEntry.addProperty("totalCount",
                                    JsonUtils.getInt(freq, "total_count", 0));
                            frequencyEntries.add(freqEntry);
                        }
                    }
                }

                if (!clinicalEntries.isEmpty()) {
                    result.add("clinical", clinicalEntries);
                }
                if (!frequencyEntries.isEmpty()) {
                    result.add("frequencies", frequencyEntries);
                }
            }
        }

        return result;
    }

    /**
     * Format a SNP summary entry from E-utilities esummary.
     */
    public JsonObject formatSnpSummary(String rsId, JsonObject summary) {
        JsonObject result = new JsonObject();
        result.addProperty("rsId", "rs" + rsId);
        result.addProperty("snpClass", JsonUtils.getString(summary, "snp_class", ""));
        result.addProperty("chrpos", JsonUtils.getString(summary, "chrpos", ""));
        result.addProperty("genes", JsonUtils.getString(summary, "genes", ""));
        result.addProperty("clinicalSignificance",
                JsonUtils.getString(summary, "clinical_significance", ""));
        result.addProperty("globalMaf", JsonUtils.getString(summary, "global_maf", ""));
        result.addProperty("url", "https://www.ncbi.nlm.nih.gov/snp/rs" + rsId);
        return result;
    }
}
