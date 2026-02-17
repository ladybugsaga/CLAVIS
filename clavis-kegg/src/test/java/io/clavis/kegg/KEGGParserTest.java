package io.clavis.kegg;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KEGGParserTest {

    @Test
    void testParseTabDelimited() {
        String input = "path:map05200\tPathways in cancer\n" +
                "path:map05210\tColorectal cancer\n" +
                "path:map05212\tPancreatic cancer\n";

        JsonObject result = KEGGParser.parseTabDelimited(input);
        assertEquals(3, result.get("totalResults").getAsInt());
        JsonArray results = result.getAsJsonArray("results");
        assertEquals("path:map05200", results.get(0).getAsJsonObject().get("id").getAsString());
        assertEquals("Pathways in cancer", results.get(0).getAsJsonObject().get("description").getAsString());
    }

    @Test
    void testParseTabDelimitedEmpty() {
        JsonObject result = KEGGParser.parseTabDelimited("");
        assertEquals(0, result.get("totalResults").getAsInt());
    }

    @Test
    void testParseTabDelimitedNull() {
        JsonObject result = KEGGParser.parseTabDelimited(null);
        assertEquals(0, result.get("totalResults").getAsInt());
    }

    @Test
    void testParseFlatFile() {
        String input = "ENTRY       hsa00010                    Pathway\n" +
                "NAME        Glycolysis / Gluconeogenesis - Homo sapiens (human)\n" +
                "CLASS       Metabolism; Carbohydrate metabolism\n" +
                "PATHWAY_MAP hsa00010  Glycolysis / Gluconeogenesis\n" +
                "GENE        2645  GCK; glucokinase [KO:K12407]\n" +
                "            2821  GPI; glucose-6-phosphate isomerase [KO:K01810]\n" +
                "///\n";

        JsonObject result = KEGGParser.parseFlatFile(input);
        assertEquals("hsa00010                    Pathway", result.get("ENTRY").getAsString());
        assertTrue(result.get("NAME").getAsString().contains("Glycolysis"));
        assertTrue(result.get("CLASS").getAsString().contains("Carbohydrate"));
        assertTrue(result.get("GENE").getAsString().contains("GCK"));
        assertTrue(result.get("GENE").getAsString().contains("GPI"));
    }

    @Test
    void testParseFlatFileEmpty() {
        JsonObject result = KEGGParser.parseFlatFile("");
        assertTrue(result.has("error"));
    }

    @Test
    void testParseSubEntries() {
        String value = "2645  GCK; glucokinase [KO:K12407]\n" +
                "2821  GPI; glucose-6-phosphate isomerase [KO:K01810]";
        JsonArray arr = KEGGParser.parseSubEntries(value);
        assertEquals(2, arr.size());
        assertEquals("2645", arr.get(0).getAsJsonObject().get("id").getAsString());
        assertTrue(arr.get(0).getAsJsonObject().get("name").getAsString().contains("GCK"));
    }

    @Test
    void testParseLinkResults() {
        String input = "hsa:7157\tpath:hsa04115\n" +
                "hsa:7157\tpath:hsa05200\n";
        JsonObject result = KEGGParser.parseTabDelimited(input);
        assertEquals(2, result.get("totalResults").getAsInt());
        assertEquals("hsa:7157", result.getAsJsonArray("results").get(0).getAsJsonObject().get("id").getAsString());
        assertEquals("path:hsa04115",
                result.getAsJsonArray("results").get(0).getAsJsonObject().get("description").getAsString());
    }
}
