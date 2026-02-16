package io.clavis.semanticscholar;

import com.google.gson.JsonObject;
import io.clavis.core.models.Paper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SemanticScholarJsonParser}.
 */
class SemanticScholarJsonParserTest {

    @Test
    void testParsePaperSearchResults() {
        String json = """
                {
                  "total": 1,
                  "data": [
                    {
                      "paperId": "abc123",
                      "title": "Deep Learning for NLP",
                      "abstract": "A survey of deep learning methods.",
                      "year": 2024,
                      "citationCount": 42,
                      "url": "https://semanticscholar.org/paper/abc123",
                      "journal": {"name": "Nature"},
                      "externalIds": {"DOI": "10.1234/test"},
                      "authors": [
                        {"authorId": "1", "name": "Alice Smith"},
                        {"authorId": "2", "name": "Bob Jones"}
                      ],
                      "fieldsOfStudy": ["Computer Science"],
                      "publicationTypes": ["JournalArticle"]
                    }
                  ]
                }
                """;

        List<Paper> papers = SemanticScholarJsonParser.parsePaperSearchResults(json);
        assertEquals(1, papers.size());

        Paper paper = papers.get(0);
        assertEquals("abc123", paper.getId());
        assertEquals("semantic_scholar", paper.getSource());
        assertEquals("Deep Learning for NLP", paper.getTitle());
        assertEquals("A survey of deep learning methods.", paper.getAbstractText());
        assertEquals("2024", paper.getPublicationDate());
        assertEquals(42, paper.getCitationCount());
        assertEquals("Nature", paper.getJournal());
        assertEquals("10.1234/test", paper.getDoi());
        assertEquals(2, paper.getAuthors().size());
        assertEquals("Alice Smith", paper.getAuthors().get(0).getName());
        assertTrue(paper.getKeywords().contains("Computer Science"));
        assertTrue(paper.getPublicationTypes().contains("JournalArticle"));
    }

    @Test
    void testParseSinglePaper() {
        String json = """
                {
                  "paperId": "xyz789",
                  "title": "Attention Is All You Need",
                  "abstract": "The Transformer architecture.",
                  "year": 2017,
                  "citationCount": 50000,
                  "url": "https://semanticscholar.org/paper/xyz789",
                  "authors": [{"authorId": "3", "name": "Vaswani"}],
                  "externalIds": {"DOI": "10.5555/test2"},
                  "journal": null,
                  "fieldsOfStudy": null,
                  "publicationTypes": null
                }
                """;

        Paper paper = SemanticScholarJsonParser.parseSinglePaper(json);
        assertNotNull(paper);
        assertEquals("xyz789", paper.getId());
        assertEquals("Attention Is All You Need", paper.getTitle());
        assertEquals(50000, paper.getCitationCount());
    }

    @Test
    void testParseCitationResults() {
        String json = """
                {
                  "data": [
                    {
                      "citingPaper": {
                        "paperId": "cite1",
                        "title": "Citing Paper",
                        "year": 2023,
                        "citationCount": 5,
                        "authors": [],
                        "url": null
                      }
                    }
                  ]
                }
                """;

        List<Paper> papers = SemanticScholarJsonParser.parseCitationResults(json);
        assertEquals(1, papers.size());
        assertEquals("cite1", papers.get(0).getId());
        assertEquals("Citing Paper", papers.get(0).getTitle());
    }

    @Test
    void testParseReferenceResults() {
        String json = """
                {
                  "data": [
                    {
                      "citedPaper": {
                        "paperId": "ref1",
                        "title": "Referenced Paper",
                        "year": 2020,
                        "citationCount": 100,
                        "authors": [{"authorId": "a1", "name": "Dr. Ref"}],
                        "url": "https://s2.org/ref1"
                      }
                    }
                  ]
                }
                """;

        List<Paper> papers = SemanticScholarJsonParser.parseReferenceResults(json);
        assertEquals(1, papers.size());
        assertEquals("ref1", papers.get(0).getId());
        assertEquals(1, papers.get(0).getAuthors().size());
    }

    @Test
    void testParseRecommendationResults() {
        String json = """
                {
                  "recommendedPapers": [
                    {
                      "paperId": "rec1",
                      "title": "Recommended Paper",
                      "year": 2024,
                      "citationCount": 10,
                      "authors": [],
                      "url": null
                    }
                  ]
                }
                """;

        List<Paper> papers = SemanticScholarJsonParser.parseRecommendationResults(json);
        assertEquals(1, papers.size());
        assertEquals("rec1", papers.get(0).getId());
    }

    @Test
    void testParseAuthorSearchResults() {
        String json = """
                {
                  "total": 1,
                  "data": [
                    {
                      "authorId": "12345",
                      "name": "Geoffrey Hinton",
                      "paperCount": 500,
                      "citationCount": 400000,
                      "hIndex": 150,
                      "url": "https://semanticscholar.org/author/12345",
                      "affiliations": ["University of Toronto"]
                    }
                  ]
                }
                """;

        JsonObject result = SemanticScholarJsonParser.parseAuthorSearchResults(json);
        assertEquals(1, result.get("total").getAsInt());
        assertEquals(1, result.getAsJsonArray("authors").size());
        JsonObject author = result.getAsJsonArray("authors").get(0).getAsJsonObject();
        assertEquals("Geoffrey Hinton", author.get("name").getAsString());
        assertEquals(150, author.get("hIndex").getAsInt());
    }

    @Test
    void testParseEmptyResults() {
        assertEquals(0, SemanticScholarJsonParser.parsePaperSearchResults("{}").size());
        assertNull(SemanticScholarJsonParser.parseSinglePaper("{}"));
        assertEquals(0, SemanticScholarJsonParser.parseCitationResults("{}").size());
        assertEquals(0, SemanticScholarJsonParser.parseReferenceResults("{}").size());
        assertEquals(0, SemanticScholarJsonParser.parseRecommendationResults("{}").size());
    }

    @Test
    void testParseInvalidJson() {
        assertEquals(0, SemanticScholarJsonParser.parsePaperSearchResults("not json").size());
        assertNull(SemanticScholarJsonParser.parseSinglePaper("not json"));
    }
}
