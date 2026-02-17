package io.clavis.kegg;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.mcp.ToolExecutionException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Concrete MCPTool implementation for KEGG tools.
 */
public class KEGGTool implements MCPTool {

    private final String name;
    private final String description;
    private final Map<String, String> properties;
    private final List<String> required;
    private final Function<JsonObject, String> executor;

    public KEGGTool(String name, String description, Map<String, String> properties,
            List<String> required, Function<JsonObject, String> executor) {
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
            prop.addProperty("type", entry.getValue().equals("integer") ? "integer" : "string");
            props.add(entry.getKey(), prop);
        }
        schema.add("properties", props);
        JsonArray req = new JsonArray();
        for (String r : required) {
            req.add(r);
        }
        schema.add("required", req);
        return schema;
    }

    @Override
    public JsonObject execute(JsonObject params) throws ToolExecutionException {
        try {
            String result = executor.apply(params);
            return com.google.gson.JsonParser.parseString(result).getAsJsonObject();
        } catch (Exception e) {
            throw new ToolExecutionException("Tool execution failed: " + e.getMessage(), e);
        }
    }
}
