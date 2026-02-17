package io.clavis.europepmc;

import io.clavis.core.models.Paper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EuropePmcJsonParserTest {

    private EuropePmcJsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new EuropePmcJsonParser();
    }

    @Test
    void testParseSearchResults() {
        String json = "{\n" +
                "  \"hitCount\": 1,\n" +
                "  \"resultList\": {\n" +
                "    \"result\": [\n" +
                "      {\n" +
                "        \"id\": \"33116279\",\n" +
                "        \"source\": \"MED\",\n" +
                "        \"title\": \"Sample Title\",\n" +
                "        \"authorString\": \"Author A, Author B\",\n" +
                "        \"journalTitle\": \"Nature\",\n" +
                "        \"pubYear\": \"2020\",\n" +
                "        \"doi\": \"10.123/456\",\n" +
                "        \"abstractText\": \"This is an abstract.\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        List<Paper> papers = parser.parseSearchResults(json);
        assertEquals(1, papers.size());

        Paper paper = papers.get(0);
        assertEquals("33116279", paper.getId());
        assertEquals("MED", paper.getSource());
        assertEquals("Sample Title", paper.getTitle());
        assertEquals("Nature", paper.getJournal());
        assertEquals("2020", paper.getPublicationDate());
        assertEquals("10.123/456", paper.getDoi());
        assertEquals("This is an abstract.", paper.getAbstractText());
        assertEquals(2, paper.getAuthors().size());
        assertEquals("Author A", paper.getAuthors().get(0).getName());
        assertEquals("https://europepmc.org/article/MED/33116279", paper.getUrl());
    }

    @Test
    void testParseEmptyResults() {
        String json = "{\"hitCount\": 0, \"resultList\": {\"result\": []}}";
        List<Paper> papers = parser.parseSearchResults(json);
        assertTrue(papers.isEmpty());
    }

    @Test
    void testParseInvalidJson() {
        String json = "{\"invalid\": \"data\"}";
        List<Paper> papers = parser.parseSearchResults(json);
        assertTrue(papers.isEmpty());
    }
}
