package io.clavis.clinicaltrials;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClinicalTrialsJsonParserTest {

    @Test
    void testParseStudySearch() {
        String json = """
                {
                  "totalCount": 2,
                  "studies": [
                    {
                      "protocolSection": {
                        "identificationModule": {
                          "nctId": "NCT04267848",
                          "briefTitle": "Study of Drug X in Lung Cancer",
                          "officialTitle": "A Randomized Study of Drug X"
                        },
                        "statusModule": {
                          "overallStatus": "RECRUITING",
                          "startDateStruct": { "date": "2020-03-15" },
                          "primaryCompletionDateStruct": { "date": "2025-12-01" }
                        },
                        "sponsorCollaboratorsModule": {
                          "leadSponsor": { "name": "National Cancer Institute" }
                        },
                        "designModule": {
                          "studyType": "INTERVENTIONAL",
                          "phases": ["PHASE3"],
                          "enrollmentInfo": { "count": "500" }
                        },
                        "conditionsModule": {
                          "conditions": ["Lung Cancer", "NSCLC"]
                        },
                        "armsInterventionsModule": {
                          "interventions": [
                            { "name": "Drug X", "type": "DRUG" },
                            { "name": "Placebo", "type": "DRUG" }
                          ]
                        },
                        "descriptionModule": {
                          "briefSummary": "This study evaluates the efficacy of Drug X."
                        }
                      }
                    },
                    {
                      "protocolSection": {
                        "identificationModule": {
                          "nctId": "NCT05000001",
                          "briefTitle": "Phase I Trial of Drug Y"
                        },
                        "statusModule": {
                          "overallStatus": "COMPLETED"
                        }
                      }
                    }
                  ]
                }
                """;

        JsonObject result = ClinicalTrialsJsonParser.parseStudySearch(json);
        assertEquals(2, result.get("totalCount").getAsInt());
        assertEquals(2, result.get("returnedCount").getAsInt());

        JsonArray trials = result.getAsJsonArray("trials");
        assertEquals(2, trials.size());

        JsonObject first = trials.get(0).getAsJsonObject();
        assertEquals("NCT04267848", first.get("nctId").getAsString());
        assertEquals("Study of Drug X in Lung Cancer", first.get("title").getAsString());
        assertEquals("RECRUITING", first.get("status").getAsString());
        assertEquals("National Cancer Institute", first.get("sponsor").getAsString());
        assertEquals("PHASE3", first.get("phase").getAsString());
        assertEquals("500", first.get("enrollment").getAsString());
        assertEquals("2020-03-15", first.get("startDate").getAsString());

        // Conditions
        JsonArray conditions = first.getAsJsonArray("conditions");
        assertEquals(2, conditions.size());
        assertEquals("Lung Cancer", conditions.get(0).getAsString());

        // Interventions
        JsonArray interventions = first.getAsJsonArray("interventions");
        assertEquals(2, interventions.size());
        assertTrue(interventions.get(0).getAsString().contains("Drug X"));

        // Second trial (minimal data)
        JsonObject second = trials.get(1).getAsJsonObject();
        assertEquals("NCT05000001", second.get("nctId").getAsString());
        assertEquals("COMPLETED", second.get("status").getAsString());
    }

    @Test
    void testParseStudyDetail() {
        String json = """
                {
                  "protocolSection": {
                    "identificationModule": {
                      "nctId": "NCT04267848",
                      "briefTitle": "Study of Drug X"
                    },
                    "statusModule": {
                      "overallStatus": "RECRUITING"
                    },
                    "eligibilityModule": {
                      "eligibilityCriteria": "Inclusion: Age >= 18",
                      "sex": "ALL",
                      "minimumAge": "18 Years",
                      "maximumAge": "75 Years"
                    },
                    "contactsLocationsModule": {
                      "locations": [
                        {
                          "facility": "Mayo Clinic",
                          "city": "Rochester",
                          "state": "Minnesota",
                          "country": "United States"
                        }
                      ]
                    }
                  }
                }
                """;

        JsonObject result = ClinicalTrialsJsonParser.parseStudyDetail(json);
        assertEquals("NCT04267848", result.get("nctId").getAsString());
        assertEquals("Inclusion: Age >= 18", result.get("eligibilityCriteria").getAsString());
        assertEquals("18 Years", result.get("minimumAge").getAsString());

        JsonArray locations = result.getAsJsonArray("locations");
        assertEquals(1, locations.size());
        assertEquals("Mayo Clinic", locations.get(0).getAsJsonObject().get("facility").getAsString());
    }

    @Test
    void testParseEmptyResponse() {
        String json = "{\"studies\":[],\"totalCount\":0}";
        JsonObject result = ClinicalTrialsJsonParser.parseStudySearch(json);
        assertEquals(0, result.get("totalCount").getAsInt());
        assertEquals(0, result.get("returnedCount").getAsInt());
    }
}
