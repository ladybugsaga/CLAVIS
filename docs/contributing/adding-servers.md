# Adding New MCP Servers

This guide walks you through adding a new biomedical database to CLAVIS.

---

## Template

Every MCP server follows the same 4-file pattern:

```
clavis-{database}/
├── pom.xml                              # Module POM
└── src/main/java/io/clavis/{database}/
    ├── {Database}Server.java            # MCPServer subclass (entry point)
    ├── {Database}Client.java            # API client (HTTP, rate limiting)
    ├── {Database}Tools.java             # MCP tool factory
    ├── parsers/
    │   └── {Database}Parser.java        # Response parser
    └── models/
        └── {Database}Model.java         # Database-specific models
```

---

## Step-by-Step

### 1. Create the module directory
```bash
mkdir -p clavis-mydb/src/main/java/io/clavis/mydb/{parsers,models}
mkdir -p clavis-mydb/src/test/java/io/clavis/mydb
```

### 2. Create `pom.xml`
```xml
<project>
    <parent>
        <groupId>io.clavis</groupId>
        <artifactId>clavis-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>clavis-mydb</artifactId>
    <dependencies>
        <dependency>
            <groupId>io.clavis</groupId>
            <artifactId>clavis-core</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 3. Create the API Client
```java
public class MyDbClient {
    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;

    public MyDbClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(10);
        this.retryPolicy = RetryPolicy.defaultPolicy();
    }

    public List<Paper> search(String query, int max) throws ApiException {
        return retryPolicy.execute(() -> {
            rateLimiter.acquire();
            // Make HTTP request, parse response, return results
        });
    }
}
```

### 4. Create MCP Tools
```java
public class MyDbTools {
    private final MyDbClient client;

    public MCPTool createSearchTool() {
        return new MCPTool() {
            public String getName() { return "search_mydb"; }
            public String getDescription() { return "Search MyDB"; }
            public JsonObject getInputSchema() { /* ... */ }
            public JsonObject execute(JsonObject params) { /* ... */ }
        };
    }
}
```

### 5. Create the Server
```java
public class MyDbServer extends MCPServer {
    public MyDbServer() { super("clavis-mydb", "1.0.0"); }

    @Override
    protected void registerTools() {
        MyDbClient client = new MyDbClient();
        MyDbTools tools = new MyDbTools(client);
        this.tools.add(tools.createSearchTool());
    }

    public static void main(String[] args) { new MyDbServer().start(); }
}
```

### 6. Add to parent POM
Add `<module>clavis-mydb</module>` to the parent `pom.xml`.

### 7. Write tests
```java
class MyDbClientTest {
    @Test void testSearchRejectsNull() { /* ... */ }
    @Test void testParserHandlesEmpty() { /* ... */ }
}
```

### 8. Build & test
```bash
mvn clean install
```

---

## Checklist
- [ ] Module directory and POM created
- [ ] API client with rate limiting and retry
- [ ] Response parser (XML or JSON)
- [ ] MCP tools with input schemas
- [ ] Server class extending MCPServer
- [ ] Unit tests for client and parser
- [ ] Module added to parent POM
- [ ] Documentation page in `docs/`
- [ ] API reference updated
