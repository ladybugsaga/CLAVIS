package io.clavis.core.util;

/**
 * Utility methods for input validation.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Static utility class
    }

    /**
     * Validates that a string is not null or empty.
     *
     * @param value the string to validate
     * @param name  the parameter name for the error message
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void requireNonEmpty(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be null or empty");
        }
    }

    /**
     * Validates that an integer is within the given range.
     *
     * @param value the value to validate
     * @param min   minimum (inclusive)
     * @param max   maximum (inclusive)
     * @param name  the parameter name for the error message
     * @throws IllegalArgumentException if the value is out of range
     */
    public static void requireInRange(int value, int min, int max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    String.format("%s must be between %d and %d, got %d", name, min, max, value));
        }
    }

    /**
     * Validates that a value is positive.
     *
     * @param value the value to validate
     * @param name  the parameter name for the error message
     * @throws IllegalArgumentException if the value is not positive
     */
    public static void requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive, got " + value);
        }
    }
}
