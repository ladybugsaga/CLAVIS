package io.clavis.core.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RateLimiter}.
 */
class RateLimiterTest {

    @Test
    @DisplayName("constructor should throw for non-positive rate")
    void testConstructorRejectsNonPositive() {
        assertThrows(IllegalArgumentException.class, () -> new RateLimiter(0));
        assertThrows(IllegalArgumentException.class, () -> new RateLimiter(-1));
    }

    @Test
    @DisplayName("tryAcquire() should succeed up to capacity")
    void testTryAcquireSucceedsUpToCapacity() {
        RateLimiter limiter = new RateLimiter(5);
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.tryAcquire(), "Should succeed at attempt " + i);
        }
    }

    @Test
    @DisplayName("tryAcquire() should fail after capacity exhausted")
    void testTryAcquireFailsAfterCapacity() {
        RateLimiter limiter = new RateLimiter(3);
        // Exhaust all tokens
        for (int i = 0; i < 3; i++) {
            assertTrue(limiter.tryAcquire());
        }
        // Next attempt should fail
        assertFalse(limiter.tryAcquire());
    }

    @Test
    @DisplayName("getAvailableTokens() should return capacity initially")
    void testInitialTokens() {
        RateLimiter limiter = new RateLimiter(10);
        assertEquals(10, limiter.getAvailableTokens());
    }

    @Test
    @DisplayName("hasAvailableTokens() should return true initially")
    void testHasAvailableTokensInitially() {
        RateLimiter limiter = new RateLimiter(5);
        assertTrue(limiter.hasAvailableTokens());
    }

    @Test
    @DisplayName("hasAvailableTokens() should return false when exhausted")
    void testHasNoTokensWhenExhausted() {
        RateLimiter limiter = new RateLimiter(1);
        limiter.tryAcquire();
        assertFalse(limiter.hasAvailableTokens());
    }

    @Test
    @DisplayName("acquire() should complete when tokens available")
    void testAcquireCompletesWithTokens() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(5);
        limiter.acquire(); // Should not block
        assertEquals(4, limiter.getAvailableTokens());
    }
}
