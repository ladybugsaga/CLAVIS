package io.clavis.core.mcp;

import com.google.gson.JsonObject;

/**
 * Interface for MCP tool implementations.
 *
 * <p>
 * Each tool represents a single capability exposed via the
 * Model Context Protocol. Tools define their name, description,
 * input schema, and execution logic.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public interface MCPTool {

    /**
     * Returns the unique name of this tool.
     *
     * @return the tool name, e.g. "search_pubmed"
     */
    String getName();

    /**
     * Returns a human-readable description of this tool.
     *
     * @return the tool description
     */
    String getDescription();

    /**
     * Returns the JSON schema defining this tool's input parameters.
     *
     * @return the input schema as a JsonObject
     */
    JsonObject getInputSchema();

    /**
     * Executes this tool with the given parameters.
     *
     * @param parameters the input parameters as JSON
     * @return the result as JSON
     * @throws ToolExecutionException if execution fails
     */
    JsonObject execute(JsonObject parameters) throws ToolExecutionException;
}
