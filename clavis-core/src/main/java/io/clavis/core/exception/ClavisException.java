package io.clavis.core.exception;

/**
 * Base checked exception for all CLAVIS operations.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ClavisException extends Exception {

    public ClavisException(String message) {
        super(message);
    }

    public ClavisException(String message, Throwable cause) {
        super(message, cause);
    }
}
