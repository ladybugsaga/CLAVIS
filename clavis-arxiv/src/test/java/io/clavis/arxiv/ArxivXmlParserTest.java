package io.clavis.arxiv;

import io.clavis.core.models.Paper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArxivXmlParserTest {

    private ArxivXmlParser parser;

    private static final String SAMPLE_ATOM = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            "xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\" " +
            "xmlns:arxiv=\"http://arxiv.org/schemas/atom\">\n" +
            "<opensearch:totalResults>42</opensearch:totalResults>\n" +
            "<entry>\n" +
            "  <id>http://arxiv.org/abs/2301.12345v1</id>\n" +
            "  <published>2023-01-30T18:00:00Z</published>\n" +
            "  <title>Attention Is All You Need Revisited</title>\n" +
            "  <summary>We revisit the transformer architecture and propose improvements.</summary>\n" +
            "  <author><name>Alice Smith</name></author>\n" +
            "  <author><name>Bob Jones</name></author>\n" +
            "  <arxiv:doi>10.1234/test</arxiv:doi>\n" +
            "  <arxiv:journal_ref>Nature 2023</arxiv:journal_ref>\n" +
            "  <link title=\"pdf\" href=\"http://arxiv.org/pdf/2301.12345v1\" rel=\"related\" type=\"application/pdf\"/>\n"
            +
            "  <category term=\"cs.AI\" scheme=\"http://arxiv.org/schemas/atom\"/>\n" +
            "  <category term=\"cs.LG\" scheme=\"http://arxiv.org/schemas/atom\"/>\n" +
            "</entry>\n" +
            "</feed>";

    @BeforeEach
    void setUp() {
        parser = new ArxivXmlParser();
    }

    @Test
    void testParseSearchResults() {
        List<Paper> papers = parser.parseSearchResults(SAMPLE_ATOM);
        assertEquals(1, papers.size());

        Paper paper = papers.get(0);
        assertEquals("2301.12345", paper.getId());
        assertEquals("arXiv", paper.getSource());
        assertEquals("Attention Is All You Need Revisited", paper.getTitle());
        assertTrue(paper.getAbstractText().contains("transformer"));
        assertEquals("2023-01-30", paper.getPublicationDate());
        assertEquals("10.1234/test", paper.getDoi());
        assertEquals("Nature 2023", paper.getJournal());
        assertEquals("https://arxiv.org/abs/2301.12345", paper.getUrl());

        assertEquals(2, paper.getAuthors().size());
        assertEquals("Alice Smith", paper.getAuthors().get(0).getName());
        assertEquals("Bob Jones", paper.getAuthors().get(1).getName());

        assertEquals(2, paper.getKeywords().size());
        assertTrue(paper.getKeywords().contains("cs.AI"));
        assertTrue(paper.getKeywords().contains("cs.LG"));
    }

    @Test
    void testGetTotalResults() {
        assertEquals(42, parser.getTotalResults(SAMPLE_ATOM));
    }

    @Test
    void testParseEmptyFeed() {
        String emptyFeed = "<?xml version=\"1.0\"?><feed xmlns=\"http://www.w3.org/2005/Atom\">" +
                "<opensearch:totalResults xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\">0</opensearch:totalResults>"
                +
                "</feed>";
        List<Paper> papers = parser.parseSearchResults(emptyFeed);
        assertTrue(papers.isEmpty());
    }

    @Test
    void testVersionStripping() {
        String xml = "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:arxiv=\"http://arxiv.org/schemas/atom\">" +
                "<entry><id>http://arxiv.org/abs/hep-ex/0307015v2</id>" +
                "<title>Test</title><summary>Test</summary>" +
                "<published>2003-07-07T00:00:00Z</published></entry></feed>";
        List<Paper> papers = parser.parseSearchResults(xml);
        assertEquals("hep-ex/0307015", papers.get(0).getId());
    }
}
