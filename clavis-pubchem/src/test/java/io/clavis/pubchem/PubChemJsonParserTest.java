package io.clavis.pubchem;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PubChemJsonParserTest {

    @Test
    void testParsePropertySearch() {
        String json = """
                {
                  "PropertyTable": {
                    "Properties": [
                      {
                        "CID": 2244,
                        "MolecularFormula": "C9H8O4",
                        "MolecularWeight": 180.16,
                        "IUPACName": "2-acetyloxybenzoic acid",
                        "CanonicalSMILES": "CC(=O)OC1=CC=CC=C1C(=O)O",
                        "InChIKey": "BSYNRYMUTXBXSQ-UHFFFAOYSA-N",
                        "XLogP": 1.2,
                        "HBondDonorCount": 1,
                        "HBondAcceptorCount": 4
                      }
                    ]
                  }
                }
                """;

        JsonObject result = PubChemJsonParser.parsePropertySearch(json);
        assertEquals(1, result.get("totalResults").getAsInt());

        JsonArray compounds = result.getAsJsonArray("compounds");
        assertEquals(1, compounds.size());

        JsonObject aspirin = compounds.get(0).getAsJsonObject();
        assertEquals(2244, aspirin.get("CID").getAsInt());
        assertEquals("C9H8O4", aspirin.get("MolecularFormula").getAsString());
        assertEquals("2-acetyloxybenzoic acid", aspirin.get("IUPACName").getAsString());
        assertTrue(aspirin.get("CanonicalSMILES").getAsString().contains("CC(=O)"));
        assertEquals(1, aspirin.get("HBondDonorCount").getAsInt());
    }

    @Test
    void testParseDescription() {
        String json = """
                {
                  "InformationList": {
                    "Information": [
                      {
                        "CID": 2244,
                        "Title": "Aspirin",
                        "Description": "Aspirin is a salicylate drug.",
                        "DescriptionSourceName": "DrugBank",
                        "DescriptionURL": "https://drugbank.ca"
                      },
                      {
                        "CID": 2244,
                        "Title": "Aspirin",
                        "Description": "A non-steroidal anti-inflammatory agent.",
                        "DescriptionSourceName": "MeSH"
                      }
                    ]
                  }
                }
                """;

        JsonObject result = PubChemJsonParser.parseDescription(json);
        assertEquals(2, result.get("totalResults").getAsInt());

        JsonArray descriptions = result.getAsJsonArray("descriptions");
        assertEquals("Aspirin", descriptions.get(0).getAsJsonObject().get("Title").getAsString());
        assertTrue(descriptions.get(0).getAsJsonObject().get("Description").getAsString().contains("salicylate"));
    }

    @Test
    void testParseSynonyms() {
        String json = """
                {
                  "InformationList": {
                    "Information": [
                      {
                        "CID": 2244,
                        "Synonym": [
                          "aspirin",
                          "acetylsalicylic acid",
                          "Acylpyrin",
                          "Aspirin",
                          "2-acetoxybenzoic acid"
                        ]
                      }
                    ]
                  }
                }
                """;

        JsonObject result = PubChemJsonParser.parseSynonyms(json);
        assertEquals(2244, result.get("CID").getAsInt());
        assertEquals(5, result.get("totalSynonyms").getAsInt());

        JsonArray synonyms = result.getAsJsonArray("synonyms");
        assertEquals(5, synonyms.size());
        assertEquals("aspirin", synonyms.get(0).getAsString());
    }

    @Test
    void testParseEmptyPropertySearch() {
        String json = "{\"PropertyTable\":{\"Properties\":[]}}";
        JsonObject result = PubChemJsonParser.parsePropertySearch(json);
        assertEquals(0, result.get("totalResults").getAsInt());
    }
}
