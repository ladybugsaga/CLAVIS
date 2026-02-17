package io.clavis.kegg;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KEGGClientTest {

    @Test
    void testClientInstantiation() {
        KEGGClient client = new KEGGClient();
        assertNotNull(client);
    }
}
