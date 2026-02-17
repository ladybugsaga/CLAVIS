package io.clavis.chembl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChEMBLJsonParserTest {

    @Test
    void parseCompoundSearch_validJson_returnsCompounds() {
        String json = "{\"molecules\": [{\"molecule_chembl_id\": \"CHEMBL25\", \"pref_name\": \"ASPIRIN\", \"molecule_properties\": {\"full_molformula\": \"C9H8O4\"}}]}";
        JsonObject result = ChEMBLJsonParser.parseCompoundSearch(json);
        assertTrue(result.has("compounds"));
        assertEquals(1, result.getAsJsonArray("compounds").size());
        assertEquals("ASPIRIN", result.getAsJsonArray("compounds").get(0).getAsJsonObject().get("name").getAsString());
    }

    @Test
    void parseMechanisms_validJson_returnsMechanisms() {
        String json = "{\"mechanisms\": [{\"mechanism_of_action\": \"COX inhibitor\", \"action_type\": \"INHIBITOR\", \"target_chembl_id\": \"CHEMBL209\"}]}";
        JsonObject result = ChEMBLJsonParser.parseMechanisms(json);
        assertTrue(result.has("mechanisms"));
        assertEquals("COX inhibitor", result.getAsJsonArray("mechanisms").get(0).getAsJsonObject().get("mechanismOfAction").getAsString());
    }
    
    @Test
    void parseBioactivity_validJson_returnsActivities() {
        String json = "{\"activities\": [{\"standard_type\": \"IC50\", \"standard_value\": \"10.5\", \"standard_units\": \"nM\", \"molecule_chembl_id\": \"CHEMBL25\"}]}";
        JsonObject result = ChEMBLJsonParser.parseBioactivity(json);
        assertTrue(result.has("activities"));
        assertEquals("IC50", result.getAsJsonArray("activities").get(0).getAsJsonObject().get("type").getAsString());
        assertEquals("10.5", result.getAsJsonArray("activities").get(0).getAsJsonObject().get("value").getAsString());
    }
}
