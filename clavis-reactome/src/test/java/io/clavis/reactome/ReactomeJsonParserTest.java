package io.clavis.reactome;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReactomeJsonParserTest {

    private ReactomeJsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new ReactomeJsonParser();
    }

    @Test
    void testFormatSearchEntry() {
        JsonObject entry = new JsonObject();
        entry.addProperty("stId", "R-HSA-1640170");
        entry.addProperty("name", "Cell Cycle");
        entry.addProperty("species", "Homo sapiens");
        entry.addProperty("typeName", "Pathway");
        entry.addProperty("compartmentName", "cytosol");
        entry.addProperty("summation", "The cell cycle covers replication and division.");

        JsonObject result = parser.formatSearchEntry(entry);
        assertEquals("R-HSA-1640170", result.get("stId").getAsString());
        assertEquals("Cell Cycle", result.get("name").getAsString());
        assertEquals("Homo sapiens", result.get("species").getAsString());
        assertEquals("Pathway", result.get("type").getAsString());
        assertTrue(result.get("url").getAsString().contains("R-HSA-1640170"));
        assertTrue(result.has("summary"));
    }

    @Test
    void testFormatPathwayDetail() {
        JsonObject pathway = new JsonObject();
        pathway.addProperty("dbId", 1640170);
        pathway.addProperty("stId", "R-HSA-1640170");
        pathway.addProperty("displayName", "Cell Cycle");
        pathway.addProperty("speciesName", "Homo sapiens");
        pathway.addProperty("schemaClass", "TopLevelPathway");
        pathway.addProperty("isInDisease", false);
        pathway.addProperty("hasDiagram", true);

        // Add summation
        com.google.gson.JsonArray summations = new com.google.gson.JsonArray();
        JsonObject sum = new JsonObject();
        sum.addProperty("text", "Cell cycle progression description.");
        summations.add(sum);
        pathway.add("summation", summations);

        JsonObject result = parser.formatPathwayDetail(pathway);
        assertEquals("R-HSA-1640170", result.get("stId").getAsString());
        assertEquals("Cell Cycle", result.get("displayName").getAsString());
        assertTrue(result.get("hasDiagram").getAsBoolean());
        assertEquals("Cell cycle progression description.", result.get("summary").getAsString());
    }

    @Test
    void testFormatParticipant() {
        JsonObject entity = new JsonObject();
        entity.addProperty("dbId", 141433);
        entity.addProperty("stId", "R-HSA-141433");
        entity.addProperty("displayName", "MAD1L1 [cytosol]");
        entity.addProperty("schemaClass", "EntityWithAccessionedSequence");

        JsonObject result = parser.formatParticipant(entity);
        assertEquals("R-HSA-141433", result.get("stId").getAsString());
        assertEquals("MAD1L1 [cytosol]", result.get("displayName").getAsString());
    }

    @Test
    void testFormatSimplePathway() {
        JsonObject pathway = new JsonObject();
        pathway.addProperty("stId", "R-HSA-69620");
        pathway.addProperty("displayName", "Cell Cycle Checkpoints");
        pathway.addProperty("speciesName", "Homo sapiens");
        pathway.addProperty("hasDiagram", true);

        JsonObject result = parser.formatSimplePathway(pathway);
        assertEquals("R-HSA-69620", result.get("stId").getAsString());
        assertEquals("Cell Cycle Checkpoints", result.get("displayName").getAsString());
        assertTrue(result.get("url").getAsString().contains("R-HSA-69620"));
    }
}
