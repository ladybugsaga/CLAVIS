package io.clavis.alphafold;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlphaFoldJsonParserTest {

    private AlphaFoldJsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new AlphaFoldJsonParser();
    }

    @Test
    void testFormatPrediction() {
        JsonObject prediction = new JsonObject();
        prediction.addProperty("entryId", "AF-P04637-F1");
        prediction.addProperty("gene", "TP53");
        prediction.addProperty("uniprotAccession", "P04637");
        prediction.addProperty("globalMetricValue", 92.5);
        prediction.addProperty("pdbUrl", "https://alphafold.ebi.ac.uk/files/AF-P04637-F1-model_v4.pdb");

        JsonObject result = parser.formatPrediction(prediction);
        assertEquals("AF-P04637-F1", result.get("entryId").getAsString());
        assertEquals("TP53", result.get("gene").getAsString());
        assertEquals(92.5, result.get("globalMetricValue").getAsDouble());
        assertTrue(result.get("pdbUrl").getAsString().endsWith(".pdb"));
    }

    @Test
    void testFormatConfidenceSummary() {
        JsonObject prediction = new JsonObject();
        prediction.addProperty("uniprotAccession", "P04637");
        prediction.addProperty("globalMetricValue", 45.0); // Low confidence

        JsonObject result = parser.formatConfidenceSummary(prediction);
        assertTrue(result.get("confidenceInterpretation").getAsString().contains("Very low"));
    }
}
