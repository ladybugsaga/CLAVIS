package io.clavis.dbsnp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DbSnpJsonParserTest {

    private DbSnpJsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new DbSnpJsonParser();
    }

    @Test
    void testFormatRefSnpBasic() {
        JsonObject refsnp = new JsonObject();
        refsnp.addProperty("refsnp_id", "7412");

        JsonObject snapshot = new JsonObject();
        snapshot.addProperty("variant_type", "snv");
        refsnp.add("primary_snapshot_data", snapshot);

        JsonObject result = parser.formatRefSnp(refsnp);
        assertEquals("rs7412", result.get("rsId").getAsString());
        assertEquals("snv", result.get("variantType").getAsString());
        assertTrue(result.get("url").getAsString().contains("rs7412"));
    }

    @Test
    void testFormatRefSnpWithClinical() {
        JsonObject refsnp = new JsonObject();
        refsnp.addProperty("refsnp_id", "121913529");

        JsonObject snapshot = new JsonObject();
        snapshot.addProperty("variant_type", "snv");

        // Add allele_annotations with clinical data
        JsonArray annotations = new JsonArray();
        JsonObject annotation = new JsonObject();
        JsonArray clinical = new JsonArray();
        JsonObject clinEntry = new JsonObject();
        JsonArray significances = new JsonArray();
        significances.add("pathogenic");
        clinEntry.add("clinical_significances", significances);
        JsonArray diseases = new JsonArray();
        diseases.add("Breast cancer");
        clinEntry.add("disease_names", diseases);
        clinEntry.addProperty("accession_version", "RCV000013424.5");
        clinical.add(clinEntry);
        annotation.add("clinical", clinical);
        annotations.add(annotation);
        snapshot.add("allele_annotations", annotations);

        refsnp.add("primary_snapshot_data", snapshot);

        JsonObject result = parser.formatRefSnp(refsnp);
        assertEquals("rs121913529", result.get("rsId").getAsString());
        assertTrue(result.has("clinical"));
        JsonArray clinResults = result.getAsJsonArray("clinical");
        assertFalse(clinResults.isEmpty());
    }

    @Test
    void testFormatRefSnpWithFrequency() {
        JsonObject refsnp = new JsonObject();
        refsnp.addProperty("refsnp_id", "7412");

        JsonObject snapshot = new JsonObject();
        snapshot.addProperty("variant_type", "snv");

        JsonArray annotations = new JsonArray();
        JsonObject annotation = new JsonObject();
        JsonArray frequency = new JsonArray();
        JsonObject freqEntry = new JsonObject();
        freqEntry.addProperty("study_name", "GnomAD");
        freqEntry.addProperty("allele_count", 1500);
        freqEntry.addProperty("total_count", 10000);
        frequency.add(freqEntry);
        annotation.add("frequency", frequency);
        annotations.add(annotation);
        snapshot.add("allele_annotations", annotations);

        refsnp.add("primary_snapshot_data", snapshot);

        JsonObject result = parser.formatRefSnp(refsnp);
        assertTrue(result.has("frequencies"));
        JsonArray freqs = result.getAsJsonArray("frequencies");
        assertEquals("GnomAD", freqs.get(0).getAsJsonObject().get("study").getAsString());
    }

    @Test
    void testFormatSnpSummary() {
        JsonObject summary = new JsonObject();
        summary.addProperty("snp_class", "snp");
        summary.addProperty("chrpos", "19:44908684");
        summary.addProperty("genes", "APOE");
        summary.addProperty("clinical_significance", "pathogenic");
        summary.addProperty("global_maf", "T=0.0773");

        JsonObject result = parser.formatSnpSummary("7412", summary);
        assertEquals("rs7412", result.get("rsId").getAsString());
        assertEquals("APOE", result.get("genes").getAsString());
        assertEquals("pathogenic", result.get("clinicalSignificance").getAsString());
        assertTrue(result.get("url").getAsString().contains("rs7412"));
    }
}
