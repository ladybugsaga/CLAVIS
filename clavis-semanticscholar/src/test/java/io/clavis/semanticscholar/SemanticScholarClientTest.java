package io.clavis.semanticscholar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SemanticScholarClient} constructor and validation.
 */
class SemanticScholarClientTest {

    @Test
    void testClientCreatesWithoutApiKey() {
        SemanticScholarClient client = new SemanticScholarClient(null);
        assertNotNull(client);
    }

    @Test
    void testClientCreatesWithEmptyApiKey() {
        SemanticScholarClient client = new SemanticScholarClient("");
        assertNotNull(client);
    }

    @Test
    void testClientCreatesWithApiKey() {
        SemanticScholarClient client = new SemanticScholarClient("test-key-123");
        assertNotNull(client);
    }

    @Test
    void testSearchValidationRejectsNullQuery() {
        SemanticScholarClient client = new SemanticScholarClient(null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.search(null, 10, null, null, null);
        });
    }

    @Test
    void testSearchValidationRejectsEmptyQuery() {
        SemanticScholarClient client = new SemanticScholarClient(null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.search("", 10, null, null, null);
        });
    }

    @Test
    void testSearchValidationRejectsInvalidMaxResults() {
        SemanticScholarClient client = new SemanticScholarClient(null);
        assertThrows(IllegalArgumentException.class, () -> {
            client.search("test", 0, null, null, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            client.search("test", 101, null, null, null);
        });
    }

    @Test
    void testRecommendationsRejectsEmptySeeds() {
        SemanticScholarClient client = new SemanticScholarClient(null);
        assertThrows(Exception.class, () -> {
            client.getRecommendations(java.util.Collections.emptyList(), 10);
        });
    }
}
