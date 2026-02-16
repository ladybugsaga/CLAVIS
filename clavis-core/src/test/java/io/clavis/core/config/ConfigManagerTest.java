package io.clavis.core.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConfigManager}.
 */
class ConfigManagerTest {

    @Test
    @DisplayName("getInstance() should return same instance")
    void testSingletonReturnssSameInstance() {
        ConfigManager instance1 = ConfigManager.getInstance();
        ConfigManager instance2 = ConfigManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("get() should return null for missing key")
    void testGetMissingKeyReturnsNull() {
        ConfigManager config = ConfigManager.getInstance();
        assertNull(config.get("NON_EXISTENT_KEY_12345"));
    }

    @Test
    @DisplayName("get() with default should return default for missing key")
    void testGetWithDefaultReturnsFallback() {
        ConfigManager config = ConfigManager.getInstance();
        assertEquals("fallback", config.get("NON_EXISTENT_KEY_12345", "fallback"));
    }

    @Test
    @DisplayName("getInt() should return default for missing key")
    void testGetIntMissingKey() {
        ConfigManager config = ConfigManager.getInstance();
        assertEquals(42, config.getInt("NON_EXISTENT_KEY_12345", 42));
    }

    @Test
    @DisplayName("getBoolean() should return default for missing key")
    void testGetBooleanMissingKey() {
        ConfigManager config = ConfigManager.getInstance();
        assertTrue(config.getBoolean("NON_EXISTENT_KEY_12345", true));
        assertFalse(config.getBoolean("NON_EXISTENT_KEY_12345", false));
    }

    @Test
    @DisplayName("getRequired() should throw for missing key")
    void testGetRequiredThrowsForMissingKey() {
        ConfigManager config = ConfigManager.getInstance();
        assertThrows(ConfigurationException.class,
                () -> config.getRequired("NON_EXISTENT_KEY_12345"));
    }

    @Test
    @DisplayName("isSet() should return false for missing key")
    void testIsSetReturnsFalseForMissing() {
        ConfigManager config = ConfigManager.getInstance();
        assertFalse(config.isSet("NON_EXISTENT_KEY_12345"));
    }

    @Test
    @DisplayName("get() should throw on null key")
    void testGetNullKeyThrows() {
        ConfigManager config = ConfigManager.getInstance();
        assertThrows(NullPointerException.class, () -> config.get(null));
    }

    @Test
    @DisplayName("isCacheEnabled() should return default true")
    void testCacheEnabledDefault() {
        ConfigManager config = ConfigManager.getInstance();
        assertTrue(config.isCacheEnabled());
    }

    @Test
    @DisplayName("getCacheTtlMinutes() should return default 60")
    void testCacheTtlDefault() {
        ConfigManager config = ConfigManager.getInstance();
        assertEquals(60, config.getCacheTtlMinutes());
    }
}
