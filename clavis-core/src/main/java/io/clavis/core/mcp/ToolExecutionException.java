package io.clavis.core.mcp;

/**
 * Exception thrown when MCP tool execution fails.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ToolExecutionException extends Exception {

    public ToolExecutionException(String message) {
        super(message);
    }

    public ToolExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
