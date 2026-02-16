package io.clavis.core.exception;

/**
 * Checked exception thrown when API rate limits are exceeded.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class RateLimitException extends ApiException {

    private final long retryAfterMs;

    public RateLimitException(String message) {
        super(message, 429);
        this.retryAfterMs = -1;
    }

    public RateLimitException(String message, long retryAfterMs) {
        super(message, 429);
        this.retryAfterMs = retryAfterMs;
    }

    /**
     * Returns the suggested wait time before retrying, in milliseconds.
     *
     * @return retry-after in ms, or -1 if unknown
     */
    public long getRetryAfterMs() {
        return retryAfterMs;
    }
}
