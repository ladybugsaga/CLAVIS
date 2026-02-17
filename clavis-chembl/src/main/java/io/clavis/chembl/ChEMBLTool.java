package io.clavis.chembl;

import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.mcp.ToolExecutionException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ChEMBLTool implements MCPTool {
    private final String name;
    private final String description;
    private final Map<String, String> properties;
    private final List<String> required;
    private final Function<JsonObject, String> executor;

    public ChEMBLTool(String name, String description, Map<String, String> properties, List<String> required, Function<JsonObject, String> executor) {
        this.name = name;
        this.description = description;
        this.properties = properties;
        this.required = required;
        this.executor = executor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public JsonObject getInputSchema() {
        JsonObject schema = new JsonObject();
        schema.addProperty("type", "object");
        JsonObject props = new JsonObject();
        
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            JsonObject prop = new JsonObject();
            prop.addProperty("type", mapType(entry.getValue()));
            prop.addProperty("description", entry.getKey()); // Using key as desc for simplicity, or we could pass description map
            props.add(entry.getKey(), prop);
        }
        
        schema.add("properties", props);
        com.google.gson.JsonArray req = new com.google.gson.JsonArray();
        for (String r : required) {
            req.add(r);
        }
        schema.add("required", req);
        return schema;
    }

    private String mapType(String type) {
        if ("integer".equals(type)) return "integer";
        return "string";
    }

    @Override
    public JsonObject execute(JsonObject params) throws ToolExecutionException {
        try {
            String result = executor.apply(params);
            
            // Result is JSON string, parse it to ensure valid JSON structure for MCP response
            // The executor returns a JSON string, but MCP expects JsonObject
            // Actually our executor returns a String which is the JSON content
            // We need to wrap it in the expected MCP response structure if not already handled
            // Wait, MCPServer.handleToolsCall wraps the result in content array.
            // But tool.execute returns JsonObject.
            // My executor returns String.
            
            // Let's parse string to JsonObject if it's an object, or wrap if not.
            // ChEMBL tools return JSON objects anyway.
            return com.google.gson.JsonParser.parseString(result).getAsJsonObject();
        } catch (Exception e) {
            throw new ToolExecutionException("Tool execution failed: " + e.getMessage(), e);
        }
    }
}
