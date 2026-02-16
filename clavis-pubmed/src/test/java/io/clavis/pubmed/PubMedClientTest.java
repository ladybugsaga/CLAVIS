package io.clavis.pubmed;

import io.clavis.core.models.Paper;
import io.clavis.pubmed.parsers.PubMedXmlParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PubMedClient} and {@link PubMedXmlParser}.
 */
class PubMedClientTest {

    @Test
    @DisplayName("constructor should accept null/empty API key and email")
    void testConstructorAcceptsNulls() {
        assertDoesNotThrow(() -> new PubMedClient(null, "test@test.com"));
        assertDoesNotThrow(() -> new PubMedClient("", "test@test.com"));
        assertDoesNotThrow(() -> new PubMedClient("key", null));
        assertDoesNotThrow(() -> new PubMedClient("key", ""));
        assertDoesNotThrow(() -> new PubMedClient(null, null));
    }

    @Test
    @DisplayName("search should reject null query")
    void testSearchRejectsNullQuery() {
        PubMedClient client = new PubMedClient("test-key", "test@test.com");
        assertThrows(IllegalArgumentException.class,
                () -> client.search(null, 10));
    }

    @Test
    @DisplayName("search should reject invalid maxResults")
    void testSearchRejectsInvalidMaxResults() {
        PubMedClient client = new PubMedClient("test-key", "test@test.com");
        assertThrows(IllegalArgumentException.class,
                () -> client.search("test", 0));
        assertThrows(IllegalArgumentException.class,
                () -> client.search("test", 10001));
    }

    @Test
    @DisplayName("fetchByPmid should reject null PMID")
    void testFetchByPmidRejectsNull() {
        PubMedClient client = new PubMedClient("test-key", "test@test.com");
        assertThrows(IllegalArgumentException.class,
                () -> client.fetchByPmid(null));
    }

    @Test
    @DisplayName("parsePmidsFromJson should parse valid JSON")
    void testParsePmidsFromJson() {
        String json = """
                {
                  "esearchresult": {
                    "count": "3",
                    "idlist": ["12345", "67890", "11111"]
                  }
                }
                """;
        List<String> pmids = PubMedXmlParser.parsePmidsFromJson(json);
        assertEquals(3, pmids.size());
        assertEquals("12345", pmids.get(0));
        assertEquals("67890", pmids.get(1));
    }

    @Test
    @DisplayName("parsePmidsFromJson should handle empty response")
    void testParsePmidsEmptyResponse() {
        String json = "{\"esearchresult\": {\"count\": \"0\", \"idlist\": []}}";
        List<String> pmids = PubMedXmlParser.parsePmidsFromJson(json);
        assertTrue(pmids.isEmpty());
    }

    @Test
    @DisplayName("parsePmidsFromJson should handle malformed JSON")
    void testParsePmidsMalformedJson() {
        List<String> pmids = PubMedXmlParser.parsePmidsFromJson("not json");
        assertTrue(pmids.isEmpty());
    }

    @Test
    @DisplayName("parsePapers should parse valid XML")
    void testParsePapersFromXml() {
        String xml = """
                <PubmedArticleSet>
                <PubmedArticle>
                  <MedlineCitation>
                    <PMID Version="1">12345678</PMID>
                    <Article>
                      <ArticleTitle>CRISPR-Cas9 Gene Editing</ArticleTitle>
                      <Abstract>
                        <AbstractText>This paper describes CRISPR technology.</AbstractText>
                      </Abstract>
                      <AuthorList>
                        <Author>
                          <LastName>Smith</LastName>
                          <ForeName>John</ForeName>
                        </Author>
                      </AuthorList>
                      <Journal>
                        <Title>Nature</Title>
                      </Journal>
                    </Article>
                  </MedlineCitation>
                  <PubmedData>
                    <ArticleIdList>
                      <ArticleId IdType="doi">10.1038/s41586-020-1234-5</ArticleId>
                    </ArticleIdList>
                  </PubmedData>
                </PubmedArticle>
                </PubmedArticleSet>
                """;

        PubMedXmlParser parser = new PubMedXmlParser();
        List<Paper> papers = parser.parsePapers(xml);

        assertEquals(1, papers.size());
        Paper paper = papers.get(0);
        assertEquals("12345678", paper.getId());
        assertEquals("CRISPR-Cas9 Gene Editing", paper.getTitle());
        assertEquals("This paper describes CRISPR technology.", paper.getAbstractText());
        assertEquals("Nature", paper.getJournal());
        assertEquals("10.1038/s41586-020-1234-5", paper.getDoi());
        assertEquals(1, paper.getAuthors().size());
        assertEquals("John Smith", paper.getAuthors().get(0).getName());
    }

    @Test
    @DisplayName("parsePapers should handle empty XML")
    void testParsePapersEmptyXml() {
        PubMedXmlParser parser = new PubMedXmlParser();
        List<Paper> papers = parser.parsePapers("<PubmedArticleSet></PubmedArticleSet>");
        assertTrue(papers.isEmpty());
    }
}
