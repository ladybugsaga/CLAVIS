# Troubleshooting

## Connection Issues

### Server won't start
**Symptom:** No tools appear in Claude Desktop after restart.

**Solutions:**
1. **Verify the JAR exists:**
   ```bash
   ls -la clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar
   ```
   If missing, rebuild: `mvn clean install`

2. **Check the path is absolute** in your Claude config — relative paths won't work

3. **Test manually:**
   ```bash
   echo '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{}}' | \
     java -jar clavis-pubmed/target/clavis-pubmed-1.0.0-SNAPSHOT.jar
   ```
   You should get a JSON response. If not, the issue is in the server.

4. **Check Java version:**
   ```bash
   java -version  # Must be 17+
   ```

---

### "Required configuration key not set"
**Symptom:** `ConfigurationException: Required configuration key 'NCBI_API_KEY' is not set`

**Solutions:**
1. Set the key in your MCP client config's `env` block
2. Or create a `.env` file in the project root
3. Or set it as a system environment variable:
   ```bash
   export NCBI_API_KEY=your_key_here
   ```

---

### Tools appear but don't work
**Symptom:** Claude sees the tools but gets errors when using them.

**Solutions:**
1. **Check API key validity** — Try the key directly:
   ```bash
   curl "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=test&api_key=YOUR_KEY"
   ```

2. **Check internet connectivity:**
   ```bash
   ping eutils.ncbi.nlm.nih.gov
   ```

3. **Check logs** in `logs/clavis.log` for error details

---

## Build Issues

### "Child module does not exist"
**Symptom:** Maven error about missing child modules.

**Solution:** Build from the parent directory:
```bash
cd CLAVIS
mvn clean install
```

### Compilation errors
**Symptom:** `BUILD FAILURE` during `mvn install`

**Solutions:**
1. Ensure Java 17+: `java -version`
2. Clean and rebuild: `mvn clean install`
3. Force dependency download: `mvn clean install -U`

### Test failures
**Symptom:** Tests fail during build.

**Solution:** Skip tests temporarily:
```bash
mvn clean install -DskipTests
```
Then investigate: `mvn test -pl clavis-core`

---

## API-Specific Issues

### PubMed: "API error: HTTP 429"
**Cause:** Rate limit exceeded (too many requests per second).

**Solution:** CLAVIS automatically handles rate limiting. If you see this:
1. Wait 10 seconds and retry
2. Ensure you have an API key configured (raises limit from 3 to 10 req/s)

### PubMed: Empty results
**Cause:** Query syntax may be too restrictive.

**Solutions:**
1. Simplify the query — try just keywords
2. Remove date/journal filters
3. Check spelling
4. Try the query on [pubmed.ncbi.nlm.nih.gov](https://pubmed.ncbi.nlm.nih.gov/) first

### PubMed: Missing abstract
**Cause:** Not all PubMed papers have abstracts (e.g., letters, editorials).

**Solution:** This is expected. The `abstract` field will be `null` for such papers.

---

## Performance Issues

### Slow startup
**Cause:** JVM cold start.

**Solutions:**
1. This is normal the first time — subsequent requests are fast
2. The MCP server stays running between queries, so startup cost is paid once

### Slow searches
**Cause:** Network latency to NCBI servers.

**Solutions:**
1. NCBI servers are in the US — latency varies by location
2. Reduce `maxResults` for faster responses
3. Enable caching: `CLAVIS_CACHE_ENABLED=true`

---

## Environment Issues

### Wrong Java version
```bash
# Check current version
java -version

# If wrong, set JAVA_HOME explicitly
export JAVA_HOME=/path/to/java-17-or-later
export PATH="$JAVA_HOME/bin:$PATH"
```

### Maven not found
```bash
# Install via package manager
sudo apt install maven        # Debian/Ubuntu
brew install maven             # macOS
sdk install maven              # SDKMAN
```

---

## Getting Help

1. **Check the [FAQ](faq.md)** for common questions
2. **Search [existing issues](https://github.com/ladybugsaga/CLAVIS/issues)**
3. **Open a new issue** with:
   - Steps to reproduce
   - Expected vs actual behavior
   - Java version (`java -version`)
   - Maven version (`mvn -version`)
   - OS and version
   - Relevant log output from `logs/clavis.log`
