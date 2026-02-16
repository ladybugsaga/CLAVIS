package io.clavis.core.mcp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for all MCP servers.
 *
 * <p>
 * Implements the MCP stdio protocol, handling JSON-RPC messages
 * over stdin/stdout. Subclasses register their tools via
 * {@link #registerTools()}.
 * </p>
 *
 * <p>
 * Follows the Template Method pattern: subclasses implement
 * abstract methods while the base class handles the protocol
 * lifecycle.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public abstract class MCPServer {

    private static final String JSONRPC_VERSION = "2.0";
    private static final String MCP_PROTOCOL_VERSION = "2024-11-05";

    protected final String name;
    protected final String version;
    protected final Logger logger;
    protected final Gson gson;
    protected final List<MCPTool> tools;

    /**
     * Creates a new MCP server.
     *
     * @param name    the server name
     * @param version the server version
     */
    protected MCPServer(String name, String version) {
        this.name = name;
        this.version = version;
        this.logger = LoggerFactory.getLogger(getClass());
        this.gson = new Gson();
        this.tools = new ArrayList<>();
    }

    /**
     * Initializes and starts the MCP server.
     * Reads JSON-RPC messages from stdin and writes responses to stdout.
     */
    public void start() {
        logger.info("Starting {} MCP Server v{}", name, version);

        registerTools();

        logger.info("{} MCP Server ready with {} tools", name, tools.size());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(System.out, true, StandardCharsets.UTF_8)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String response = handleMessage(line);
                if (response != null) {
                    writer.println(response);
                    writer.flush();
                }
            }
        } catch (IOException e) {
            logger.error("I/O error in MCP server", e);
        }

        logger.info("{} MCP Server stopped", name);
    }

    /**
     * Registers all tools this server provides.
     * Subclasses must add tools to the {@code tools} list.
     */
    protected abstract void registerTools();

    /**
     * Handles a single JSON-RPC message.
     *
     * @param message the raw JSON message
     * @return the JSON response, or null if no response needed
     */
    protected String handleMessage(String message) {
        try {
            JsonObject request = gson.fromJson(message, JsonObject.class);
            String method = request.has("method") ? request.get("method").getAsString() : null;

            if (method == null) {
                return null;
            }

            Object id = request.has("id") ? request.get("id") : null;

            return switch (method) {
                case "initialize" -> handleInitialize(id);
                case "tools/list" -> handleToolsList(id);
                case "tools/call" -> handleToolsCall(id, request.getAsJsonObject("params"));
                case "notifications/initialized" -> null; // Notification, no response
                default -> {
                    logger.warn("Unknown method: {}", method);
                    yield createErrorResponse(id, -32601, "Method not found: " + method);
                }
            };
        } catch (Exception e) {
            logger.error("Error handling message", e);
            return createErrorResponse(null, -32700, "Parse error: " + e.getMessage());
        }
    }

    private String handleInitialize(Object id) {
        JsonObject result = new JsonObject();
        result.addProperty("protocolVersion", MCP_PROTOCOL_VERSION);

        JsonObject capabilities = new JsonObject();
        JsonObject toolsCap = new JsonObject();
        capabilities.add("tools", toolsCap);
        result.add("capabilities", capabilities);

        JsonObject serverInfo = new JsonObject();
        serverInfo.addProperty("name", name);
        serverInfo.addProperty("version", version);
        result.add("serverInfo", serverInfo);

        return createResponse(id, result);
    }

    private String handleToolsList(Object id) {
        JsonObject result = new JsonObject();
        JsonArray toolsArray = new JsonArray();

        for (MCPTool tool : tools) {
            JsonObject toolObj = new JsonObject();
            toolObj.addProperty("name", tool.getName());
            toolObj.addProperty("description", tool.getDescription());
            toolObj.add("inputSchema", tool.getInputSchema());
            toolsArray.add(toolObj);
        }

        result.add("tools", toolsArray);
        return createResponse(id, result);
    }

    private String handleToolsCall(Object id, JsonObject params) {
        String toolName = params.get("name").getAsString();
        JsonObject arguments = params.has("arguments")
                ? params.getAsJsonObject("arguments")
                : new JsonObject();

        MCPTool tool = findTool(toolName);
        if (tool == null) {
            return createErrorResponse(id, -32602, "Unknown tool: " + toolName);
        }

        try {
            logger.info("Executing tool: {}", toolName);
            JsonObject toolResult = tool.execute(arguments);

            JsonObject result = new JsonObject();
            JsonArray content = new JsonArray();
            JsonObject textContent = new JsonObject();
            textContent.addProperty("type", "text");
            textContent.addProperty("text", gson.toJson(toolResult));
            content.add(textContent);
            result.add("content", content);

            return createResponse(id, result);
        } catch (ToolExecutionException e) {
            logger.error("Tool execution failed: {}", toolName, e);

            JsonObject result = new JsonObject();
            JsonArray content = new JsonArray();
            JsonObject textContent = new JsonObject();
            textContent.addProperty("type", "text");
            textContent.addProperty("text", "Error: " + e.getMessage());
            content.add(textContent);
            result.add("content", content);
            result.addProperty("isError", true);

            return createResponse(id, result);
        }
    }

    private MCPTool findTool(String name) {
        for (MCPTool tool : tools) {
            if (tool.getName().equals(name)) {
                return tool;
            }
        }
        return null;
    }

    private String createResponse(Object id, JsonObject result) {
        JsonObject response = new JsonObject();
        response.addProperty("jsonrpc", JSONRPC_VERSION);
        if (id != null) {
            response.add("id", gson.toJsonTree(id));
        }
        response.add("result", result);
        return gson.toJson(response);
    }

    private String createErrorResponse(Object id, int code, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("jsonrpc", JSONRPC_VERSION);
        if (id != null) {
            response.add("id", gson.toJsonTree(id));
        }
        JsonObject error = new JsonObject();
        error.addProperty("code", code);
        error.addProperty("message", message);
        response.add("error", error);
        return gson.toJson(response);
    }

    /**
     * Returns an unmodifiable list of registered tools.
     *
     * @return the registered tools
     */
    public List<MCPTool> getTools() {
        return Collections.unmodifiableList(tools);
    }

    /**
     * Returns the server name.
     *
     * @return the server name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the server version.
     *
     * @return the server version
     */
    public String getVersion() {
        return version;
    }
}
