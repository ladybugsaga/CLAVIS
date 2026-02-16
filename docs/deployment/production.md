# Production Deployment

## Checklist

- [ ] Build with `mvn clean package -DskipTests`
- [ ] Set all API keys via environment variables (not `.env`)
- [ ] Configure logging to file (`logback.xml`)
- [ ] Set memory limits (`-Xmx`)
- [ ] Enable caching (`CLAVIS_CACHE_ENABLED=true`)
- [ ] Set log level to `INFO` (not `DEBUG`)
- [ ] Monitor error rates and latency

---

## Recommended JVM Settings

```bash
java \
  -Xmx512m \
  -XX:+UseZGC \
  -XX:+UseCompressedOops \
  -Djava.security.egd=file:/dev/urandom \
  -jar clavis-pubmed-1.0.0-SNAPSHOT.jar
```

| Flag | Purpose |
|------|---------|
| `-Xmx512m` | Max heap (adjust per load) |
| `-XX:+UseZGC` | Low-latency GC (Java 17+) |
| `-XX:+UseCompressedOops` | Reduce memory overhead |
| `-Djava.security.egd=...` | Faster startup |

---

## Systemd Service (Linux)

```ini
# /etc/systemd/system/clavis-pubmed.service
[Unit]
Description=CLAVIS PubMed MCP Server
After=network.target

[Service]
Type=simple
User=clavis
WorkingDirectory=/opt/clavis
ExecStart=/usr/bin/java -Xmx512m -jar /opt/clavis/clavis-pubmed.jar
Restart=on-failure
RestartSec=5
StandardInput=socket

Environment=NCBI_API_KEY=your_key
Environment=NCBI_EMAIL=your_email
Environment=CLAVIS_LOG_LEVEL=INFO
Environment=CLAVIS_CACHE_ENABLED=true

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable clavis-pubmed
sudo systemctl start clavis-pubmed
```

---

## Monitoring

### Log monitoring
```bash
tail -f /opt/clavis/logs/clavis.log
```

### Health check
```bash
echo '{"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}' | \
  java -jar clavis-pubmed.jar | jq .
```

### Key metrics to watch
- Response latency (should be <1s)
- Error rate (should be <1%)
- Memory usage (should be stable)
- Cache hit ratio (higher = better)

---

## Security Hardening

1. **Run as unprivileged user**: Create a `clavis` system user
2. **No inbound ports**: MCP uses stdin/stdout only
3. **API keys**: Use environment variables, not files
4. **File permissions**: `chmod 755` on JARs, `700` on config
5. **Log rotation**: Already configured in `logback.xml`

---

## Next Steps
- **[Docker Deployment](docker.md)** — Container-based deployment
- **[Performance](../performance.md)** — Tuning guides
- **[Security](../security.md)** — Full security practices
