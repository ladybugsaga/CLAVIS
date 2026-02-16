package io.clavis.uniprot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UniProtClient input validation.
 */
class UniProtClientTest {

    @Test
    void constructor_initializes() {
        UniProtClient client = new UniProtClient();
        assertNotNull(client);
    }

    @Test
    void searchProteins_nullQuery_throwsException() {
        UniProtClient client = new UniProtClient();
        assertThrows(IllegalArgumentException.class,
                () -> client.searchProteins(null, 10, null, null));
    }

    @Test
    void searchProteins_emptyQuery_throwsException() {
        UniProtClient client = new UniProtClient();
        assertThrows(IllegalArgumentException.class,
                () -> client.searchProteins("", 10, null, null));
    }

    @Test
    void searchProteins_blankQuery_throwsException() {
        UniProtClient client = new UniProtClient();
        assertThrows(IllegalArgumentException.class,
                () -> client.searchProteins("   ", 10, null, null));
    }
}
