package io.clavis.uniprot;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UniProtJsonParser.
 */
class UniProtJsonParserTest {

    @Test
    void parseSearchResults_validData() {
        String json = """
                {
                  "results": [
                    {
                      "primaryAccession": "P01308",
                      "uniProtkbId": "INS_HUMAN",
                      "proteinDescription": {
                        "recommendedName": {
                          "fullName": { "value": "Insulin" }
                        }
                      },
                      "genes": [{ "geneName": { "value": "INS" } }],
                      "organism": { "scientificName": "Homo sapiens" },
                      "sequence": { "length": 110 },
                      "comments": [
                        {
                          "commentType": "FUNCTION",
                          "texts": [{ "value": "Decreases blood glucose" }]
                        }
                      ]
                    }
                  ]
                }
                """;
        JsonObject result = UniProtJsonParser.parseSearchResults(json);

        assertEquals(1, result.get("totalResults").getAsInt());
        JsonObject protein = result.getAsJsonArray("proteins").get(0).getAsJsonObject();
        assertEquals("P01308", protein.get("accession").getAsString());
        assertEquals("INS_HUMAN", protein.get("entryId").getAsString());
        assertEquals("Insulin", protein.get("proteinName").getAsString());
        assertEquals("INS", protein.getAsJsonArray("genes").get(0).getAsString());
        assertEquals("Homo sapiens", protein.get("organism").getAsString());
        assertEquals(110, protein.get("length").getAsInt());
        assertEquals("Decreases blood glucose", protein.get("function").getAsString());
    }

    @Test
    void parseSearchResults_emptyResults() {
        String json = """
                { "results": [] }
                """;
        JsonObject result = UniProtJsonParser.parseSearchResults(json);
        assertEquals(0, result.get("totalResults").getAsInt());
        assertEquals(0, result.getAsJsonArray("proteins").size());
    }

    @Test
    void parseSearchResults_invalidJson() {
        JsonObject result = UniProtJsonParser.parseSearchResults("not json");
        assertEquals(0, result.get("totalResults").getAsInt());
    }

    @Test
    void parseProteinDetail_fullEntry() {
        String json = """
                {
                  "primaryAccession": "P01308",
                  "uniProtkbId": "INS_HUMAN",
                  "proteinDescription": {
                    "recommendedName": { "fullName": { "value": "Insulin" } }
                  },
                  "genes": [{ "geneName": { "value": "INS" } }],
                  "organism": {
                    "scientificName": "Homo sapiens",
                    "commonName": "Human",
                    "taxonId": 9606
                  },
                  "sequence": {
                    "value": "MALWMRLLPLL",
                    "length": 110,
                    "molWeight": 11981
                  },
                  "comments": [
                    {
                      "commentType": "FUNCTION",
                      "texts": [{ "value": "Decreases blood glucose" }]
                    },
                    {
                      "commentType": "SUBCELLULAR LOCATION",
                      "texts": [{ "value": "Secreted" }]
                    },
                    {
                      "commentType": "SUBUNIT",
                      "texts": [{ "value": "Heterodimer of B and A chains" }]
                    },
                    {
                      "commentType": "DISEASE",
                      "disease": { "diseaseId": "Diabetes mellitus" }
                    }
                  ],
                  "uniProtKBCrossReferences": [
                    { "database": "PDB", "id": "1A7F" },
                    { "database": "PDB", "id": "1AI0" },
                    { "database": "Pfam", "id": "PF00049" }
                  ],
                  "features": [
                    {
                      "type": "Signal peptide",
                      "description": "Signal",
                      "location": { "start": { "value": 1 }, "end": { "value": 24 } }
                    },
                    {
                      "type": "Chain",
                      "description": "Insulin B chain",
                      "location": { "start": { "value": 25 }, "end": { "value": 54 } }
                    },
                    {
                      "type": "Helix",
                      "description": "Some helix",
                      "location": { "start": { "value": 30 }, "end": { "value": 40 } }
                    }
                  ]
                }
                """;
        JsonObject result = UniProtJsonParser.parseProteinDetail(json);

        assertEquals("P01308", result.get("accession").getAsString());
        assertEquals("Insulin", result.get("proteinName").getAsString());
        assertEquals("INS", result.getAsJsonArray("genes").get(0).getAsString());
        assertEquals("Homo sapiens", result.getAsJsonObject("organism").get("scientificName").getAsString());
        assertEquals(9606, result.getAsJsonObject("organism").get("taxonId").getAsInt());
        assertEquals(110, result.getAsJsonObject("sequence").get("length").getAsInt());
        assertEquals("Decreases blood glucose", result.get("function").getAsString());
        assertEquals("Secreted", result.get("subcellularLocation").getAsString());
        assertEquals("Heterodimer of B and A chains", result.get("subunit").getAsString());
        assertTrue(result.has("diseases"));
        assertEquals("Diabetes mellitus", result.getAsJsonArray("diseases").get(0).getAsString());

        // PDB — only PDB entries, not Pfam
        assertEquals(2, result.getAsJsonArray("pdbStructures").size());
        assertEquals("1A7F", result.getAsJsonArray("pdbStructures").get(0).getAsString());

        // Features — Signal peptide and Chain but NOT Helix
        assertEquals(2, result.getAsJsonArray("features").size());
    }

    @Test
    void parseProteinDetail_emptyJson() {
        JsonObject result = UniProtJsonParser.parseProteinDetail("{}");
        assertNotNull(result);
    }

    @Test
    void parseProteinDetail_submittedName() {
        String json = """
                {
                  "primaryAccession": "A0A000",
                  "proteinDescription": {
                    "submittedName": [
                      { "fullName": { "value": "Uncharacterized protein" } }
                    ]
                  }
                }
                """;
        JsonObject result = UniProtJsonParser.parseProteinDetail(json);
        assertEquals("Uncharacterized protein", result.get("proteinName").getAsString());
    }
}
