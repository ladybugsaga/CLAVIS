package io.clavis.core.http;

import io.clavis.core.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RetryPolicy}.
 */
class RetryPolicyTest {

    @Test
    @DisplayName("execute() should return result on first success")
    void testExecuteSucceedsFirstAttempt() throws ApiException {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofMillis(10));
        String result = policy.execute(() -> "success");
        assertEquals("success", result);
    }

    @Test
    @DisplayName("execute() should retry on failure then succeed")
    void testExecuteRetriesAndSucceeds() throws ApiException {
        RetryPolicy policy = new RetryPolicy(3, Duration.ofMillis(10));
        AtomicInteger attempts = new AtomicInteger(0);

        String result = policy.execute(() -> {
            if (attempts.incrementAndGet() < 3) {
                throw new RuntimeException("transient failure");
            }
            return "eventually succeeded";
        });

        assertEquals("eventually succeeded", result);
        assertEquals(3, attempts.get());
    }

    @Test
    @DisplayName("execute() should throw ApiException after all retries exhausted")
    void testExecuteThrowsAfterMaxRetries() {
        RetryPolicy policy = new RetryPolicy(2, Duration.ofMillis(10));

        assertThrows(ApiException.class, () -> policy.execute(() -> {
            throw new RuntimeException("always fails");
        }));
    }

    @Test
    @DisplayName("defaultPolicy() should create policy with 3 retries")
    void testDefaultPolicy() {
        RetryPolicy policy = RetryPolicy.defaultPolicy();
        assertEquals(3, policy.getMaxRetries());
    }

    @Test
    @DisplayName("constructor should reject negative retries")
    void testConstructorRejectsNegativeRetries() {
        assertThrows(IllegalArgumentException.class,
                () -> new RetryPolicy(-1, Duration.ofSeconds(1)));
    }

    @Test
    @DisplayName("constructor should reject null delay")
    void testConstructorRejectsNullDelay() {
        assertThrows(IllegalArgumentException.class,
                () -> new RetryPolicy(3, null));
    }
}
