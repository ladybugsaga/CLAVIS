package io.clavis.core.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Thread-safe singleton for centralized configuration management.
 *
 * <p>Loads configuration from environment variables and {@code .env} file.
 * Uses Bill Pugh Singleton pattern for lazy, thread-safe initialization.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * ConfigManager config = ConfigManager.getInstance();
 * String apiKey = config.get("NCBI_API_KEY");
 * int timeout = config.getInt("TIMEOUT", 30);
 * }</pre>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public final class ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private final Dotenv dotenv;

    private ConfigManager() {
        this.dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        logger.info("ConfigManager initialized");
    }

    private static final class LazyHolder {
        static final ConfigManager INSTANCE = new ConfigManager();
    }

    /**
     * Returns the singleton instance of ConfigManager.
     *
     * @return the ConfigManager instance, never null
     */
    public static ConfigManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Gets a configuration value by key.
     *
     * @param key the configuration key (cannot be null)
     * @return the value, or null if not found
     * @throws IllegalArgumentException if key is null
     */
    public String get(String key) {
        Objects.requireNonNull(key, "Configuration key cannot be null");
        String value = dotenv.get(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value;
    }

    /**
     * Gets a configuration value with a default fallback.
     *
     * @param key          the configuration key (cannot be null)
     * @param defaultValue the default value if key is not found
     * @return the value, or defaultValue if not found
     * @throws IllegalArgumentException if key is null
     */
    public String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets a configuration value as an integer.
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key is not found or not a valid integer
     * @return the integer value, or defaultValue on error
     */
    public int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for key '{}': {}", key, value);
            return defaultValue;
        }
    }

    /**
     * Gets a configuration value as a boolean.
     *
     * @param key          the configuration key
     * @param defaultValue the default value if key is not found
     * @return the boolean value, or defaultValue on error
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    /**
     * Gets a required configuration value.
     *
     * @param key the configuration key
     * @return the value, never null
     * @throws ConfigurationException if the key is not set
     */
    public String getRequired(String key) {
        String value = get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new ConfigurationException(
                    String.format("Required configuration key '%s' is not set. "
                            + "Add it to your .env file or set it as an environment variable.", key));
        }
        return value;
    }

    /**
     * Checks if a configuration key is set and non-empty.
     *
     * @param key the configuration key
     * @return true if the key is set and non-empty
     */
    public boolean isSet(String key) {
        String value = get(key);
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Gets the NCBI API key for PubMed access.
     *
     * @return the API key
     * @throws ConfigurationException if not set
     */
    public String getNcbiApiKey() {
        return getRequired("NCBI_API_KEY");
    }

    /**
     * Gets the NCBI email for PubMed access.
     *
     * @return the email address
     * @throws ConfigurationException if not set
     */
    public String getNcbiEmail() {
        return getRequired("NCBI_EMAIL");
    }

    /**
     * Gets the Semantic Scholar API key.
     *
     * @return the API key, or null if not set
     */
    public String getSemanticScholarApiKey() {
        return get("SEMANTIC_SCHOLAR_API_KEY");
    }

    /**
     * Gets the DrugBank API key.
     *
     * @return the API key
     * @throws ConfigurationException if not set
     */
    public String getDrugBankApiKey() {
        return getRequired("DRUGBANK_API_KEY");
    }

    /**
     * Gets the configured log level.
     *
     * @return the log level string, defaults to "INFO"
     */
    public String getLogLevel() {
        return get("CLAVIS_LOG_LEVEL", "INFO");
    }

    /**
     * Checks if caching is enabled.
     *
     * @return true if caching is enabled, defaults to true
     */
    public boolean isCacheEnabled() {
        return getBoolean("CLAVIS_CACHE_ENABLED", true);
    }

    /**
     * Gets the cache TTL in minutes.
     *
     * @return cache TTL in minutes, defaults to 60
     */
    public int getCacheTtlMinutes() {
        return getInt("CLAVIS_CACHE_TTL_MINUTES", 60);
    }
}
