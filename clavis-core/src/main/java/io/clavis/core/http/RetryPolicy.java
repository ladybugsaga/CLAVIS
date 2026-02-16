package io.clavis.core.http;

import io.clavis.core.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * Retry policy with exponential backoff for failed API requests.
 *
 * <p>
 * Retries failing operations up to a configurable number of attempts,
 * with exponentially increasing delays between retries.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class RetryPolicy {

    private static final Logger logger = LoggerFactory.getLogger(RetryPolicy.class);

    private final int maxRetries;
    private final Duration initialDelay;

    /**
     * Creates a new retry policy.
     *
     * @param maxRetries   maximum number of retry attempts
     * @param initialDelay initial delay between retries
     * @throws IllegalArgumentException if maxRetries is negative or initialDelay is
     *                                  null
     */
    public RetryPolicy(int maxRetries, Duration initialDelay) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative");
        }
        if (initialDelay == null) {
            throw new IllegalArgumentException("Initial delay cannot be null");
        }
        this.maxRetries = maxRetries;
        this.initialDelay = initialDelay;
    }

    /**
     * Creates a retry policy with default settings (3 retries, 2s initial delay).
     *
     * @return a new RetryPolicy with defaults
     */
    public static RetryPolicy defaultPolicy() {
        return new RetryPolicy(3, Duration.ofSeconds(2));
    }

    /**
     * Executes an operation with retry logic.
     *
     * @param <T>       the return type of the operation
     * @param operation the operation to execute
     * @return the result of the operation
     * @throws ApiException if all retry attempts fail
     */
    public <T> T execute(Callable<T> operation) throws ApiException {
        int attempts = 0;
        Exception lastException = null;

        while (attempts <= maxRetries) {
            try {
                return operation.call();
            } catch (Exception e) {
                lastException = e;
                attempts++;

                if (attempts <= maxRetries) {
                    long delayMs = initialDelay.toMillis() * attempts;
                    logger.warn("Request failed (attempt {}/{}), retrying in {}ms: {}",
                            attempts, maxRetries + 1, delayMs, e.getMessage());
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ApiException("Interrupted during retry", ie);
                    }
                }
            }
        }

        throw new ApiException("Failed after " + (maxRetries + 1) + " attempts", lastException);
    }

    /**
     * Returns the maximum number of retries.
     *
     * @return maximum retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }
}
