package io.clavis.core.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe token bucket rate limiter.
 *
 * <p>
 * Allows bursts up to the configured capacity, then refills
 * tokens at a constant rate. Safe for concurrent use from
 * multiple threads.
 * </p>
 *
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * RateLimiter limiter = new RateLimiter(10); // 10 requests/sec
 * limiter.acquire(); // blocks until permitted
 * // make request
 * }</pre>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class RateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);
    private static final long WAIT_INTERVAL_MS = 50;

    private final int capacity;
    private final int refillRate;
    private final AtomicInteger tokens;
    private final AtomicLong lastRefillTimeNanos;

    /**
     * Creates a new rate limiter.
     *
     * @param requestsPerSecond maximum requests per second
     * @throws IllegalArgumentException if requestsPerSecond is not positive
     */
    public RateLimiter(int requestsPerSecond) {
        if (requestsPerSecond <= 0) {
            throw new IllegalArgumentException("Requests per second must be positive");
        }
        this.capacity = requestsPerSecond;
        this.refillRate = requestsPerSecond;
        this.tokens = new AtomicInteger(capacity);
        this.lastRefillTimeNanos = new AtomicLong(System.nanoTime());
        logger.debug("RateLimiter created: {} req/s", requestsPerSecond);
    }

    /**
     * Attempts to acquire permission for one request without blocking.
     *
     * @return true if the request is allowed, false if rate limited
     */
    public boolean tryAcquire() {
        refillTokens();
        int current;
        do {
            current = tokens.get();
            if (current <= 0) {
                return false;
            }
        } while (!tokens.compareAndSet(current, current - 1));
        return true;
    }

    /**
     * Blocks until permission is granted for one request.
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public void acquire() throws InterruptedException {
        while (!tryAcquire()) {
            Thread.sleep(WAIT_INTERVAL_MS);
        }
    }

    /**
     * Returns current number of available tokens.
     *
     * @return available tokens
     */
    public int getAvailableTokens() {
        refillTokens();
        return tokens.get();
    }

    /**
     * Checks if any tokens are available.
     *
     * @return true if at least one token is available
     */
    public boolean hasAvailableTokens() {
        return getAvailableTokens() > 0;
    }

    private void refillTokens() {
        long now = System.nanoTime();
        long lastRefill = lastRefillTimeNanos.get();
        long elapsedNanos = now - lastRefill;
        long elapsedSeconds = TimeUnit.NANOSECONDS.toSeconds(elapsedNanos);

        if (elapsedSeconds > 0 && lastRefillTimeNanos.compareAndSet(lastRefill, now)) {
            int tokensToAdd = (int) (elapsedSeconds * refillRate);
            int current;
            int next;
            do {
                current = tokens.get();
                next = Math.min(capacity, current + tokensToAdd);
            } while (!tokens.compareAndSet(current, next));
            logger.trace("Refilled {} tokens, available: {}", tokensToAdd, next);
        }
    }
}
