# Performance Tuning

## Benchmarks

Typical performance on a standard machine:

| Operation | Latency | Notes |
|-----------|---------|-------|
| Server startup | ~500ms | JVM initialization |
| `initialize` | <1ms | Local only |
| `tools/list` | <1ms | Local only |
| `search_pubmed` (10 results) | 300–800ms | Network dependent |
| `get_pubmed_paper` | 200–500ms | Single fetch |
| `get_related_papers` | 500–1000ms | Two API calls |

---

## Caching

### Enable caching (default: on)
```bash
CLAVIS_CACHE_ENABLED=true
CLAVIS_CACHE_TTL_MINUTES=60
```

### How caching works
- **First request**: Hits the biomedical API, caches the result
- **Subsequent requests** (same query within TTL): Returns cached result instantly (<1ms)
- **After TTL expires**: Fetches fresh data from the API

### Cache memory usage
- ~1 KB per cached paper
- 1,000 cached papers ≈ 1 MB
- Cache is automatically cleaned every 60 seconds

### Disable caching for real-time data
```bash
CLAVIS_CACHE_ENABLED=false
```

---

## Rate Limiting

### Token bucket algorithm
CLAVIS uses a token bucket rate limiter that:
- Allows bursts up to the capacity (e.g., 10 immediate requests)
- Refills tokens at a constant rate (e.g., 10 per second)
- Automatically queues requests when tokens are exhausted
- Never returns rate-limit errors to the AI client

### Per-server limits
| Server | Default Rate | With API Key |
|--------|-------------|--------------|
| PubMed | 3 req/s | 10 req/s |
| Semantic Scholar | 1 req/s | 10 req/s |
| ChEMBL | 5 req/s | 5 req/s |
| Europe PMC | 10 req/s | N/A |
| Others | 10 req/s | N/A |

---

## JVM Tuning

### Memory
Default heap is usually sufficient. For heavy usage:
```bash
java -Xmx512m -jar clavis-pubmed-1.0.0-SNAPSHOT.jar
```

### Garbage collection
For low-latency:
```bash
java -XX:+UseZGC -jar clavis-pubmed-1.0.0-SNAPSHOT.jar
```

### Startup time
For faster startup (Java 21+):
```bash
java -XX:+UseCompressedOops -jar clavis-pubmed-1.0.0-SNAPSHOT.jar
```

---

## Network Optimization

### Connection pooling
OkHttp automatically pools HTTP connections. Default: 5 idle connections, 5-minute keep-alive.

### Timeouts
| Setting | Default | Description |
|---------|---------|-------------|
| Connect timeout | 30s | TCP connection establishment |
| Read timeout | 30s | Waiting for response data |
| Write timeout | 30s | Sending request data |

### Retry policy
Failed requests are retried up to 3 times with exponential backoff:
- Attempt 1: immediate
- Attempt 2: after 2s
- Attempt 3: after 4s
- Attempt 4: after 6s (final)

---

## Monitoring

### Structured logging
CLAVIS logs all API requests and responses:
```
[2024-01-15 10:30:45.123] [INFO] [pubmed] API request: endpoint=esearch+efetch, params={query=CRISPR, maxResults=20}
[2024-01-15 10:30:45.890] [INFO] [pubmed] API response: status=200, duration=767ms
```

### Key metrics to watch
- **Response latency**: Should be <1s for most queries
- **Error rate**: Should be <1%
- **Cache hit rate**: Higher is better (check logs)

---

## Next Steps
- **[Architecture](architecture.md)** — System design details
- **[Deployment](deployment/production.md)** — Production deployment
- **[Troubleshooting](troubleshooting.md)** — Performance issues
