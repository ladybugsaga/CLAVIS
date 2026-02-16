package io.clavis.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Structured logger for consistent, machine-parseable log output.
 *
 * <p>
 * Wraps SLF4J with methods for logging API requests, responses,
 * and events with structured key-value context.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class StructuredLogger {

    private final Logger logger;

    /**
     * Creates a structured logger for the given class.
     *
     * @param clazz the class to log for
     */
    public StructuredLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    /**
     * Logs an API request at INFO level.
     *
     * @param database the database name
     * @param endpoint the API endpoint
     * @param params   request parameters
     */
    public void logApiRequest(String database, String endpoint, Map<String, Object> params) {
        logger.info("[{}] API request: endpoint={}, params={}", database, endpoint, params);
    }

    /**
     * Logs an API response at INFO level.
     *
     * @param database   the database name
     * @param statusCode the HTTP status code
     * @param durationMs the request duration in milliseconds
     */
    public void logApiResponse(String database, int statusCode, long durationMs) {
        logger.info("[{}] API response: status={}, duration={}ms", database, statusCode, durationMs);
    }

    /**
     * Logs an API error at ERROR level.
     *
     * @param database the database name
     * @param error    the error message
     * @param cause    the exception
     */
    public void logApiError(String database, String error, Throwable cause) {
        logger.error("[{}] API error: {}", database, error, cause);
    }

    /**
     * Logs a general event.
     *
     * @param event the event name
     * @param data  event data
     */
    public void logEvent(String event, Map<String, Object> data) {
        logger.info("Event: {} data={}", event, data);
    }
}
