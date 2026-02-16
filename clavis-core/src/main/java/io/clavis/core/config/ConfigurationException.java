package io.clavis.core.config;

/**
 * Unchecked exception thrown when required configuration is missing or invalid.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ConfigurationException extends RuntimeException {

    /**
     * Creates a new ConfigurationException with a message.
     *
     * @param message the error message
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Creates a new ConfigurationException with a message and cause.
     *
     * @param message the error message
     * @param cause   the underlying cause
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
