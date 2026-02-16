package io.clavis.core.exception;

/**
 * Checked exception for API communication errors.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ApiException extends ClavisException {

    private final int statusCode;

    public ApiException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Returns the HTTP status code, or -1 if not applicable.
     *
     * @return the HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }
}
