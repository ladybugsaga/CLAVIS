package io.clavis.core.exception;

/**
 * Unchecked exception thrown when input validation fails.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
