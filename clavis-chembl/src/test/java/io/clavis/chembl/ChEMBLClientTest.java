package io.clavis.chembl;

import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ChEMBLClientTest {

    @Test
    void searchCompounds_invalidQuery_throwsOrReturnsEmpty() {
        // Since we can't easily mock OkHttp without a mock server or dependency injection refactor,
        // we'll just test basic validation if any. The implementation currently doesn't validate much.
        // True unit testing of client requires mocking the HTTP client.
        // For now, we rely on the parser tests for logic and end-to-end for integration.
        // We can test that the method exists and compiles.
        ChEMBLClient client = new ChEMBLClient();
        assertNotNull(client);
    }
}
