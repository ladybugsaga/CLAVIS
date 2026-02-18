package io.clavis.openfda;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.mcp.ToolExecutionException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Concrete implementation of MCPTool for OpenFDA.
 */
public class OpenFdaTool implements MCPTool {
    private final String name;
    private final String description;
    private final Map<String, String> properties;
    private final List<String> required;
    private final Function<Map<String, Object>, String> executor;

    public OpenFdaTool(String name, String description, Map<String, String> properties, List<String> required, Function<Map<String, Object>, String> executor) {
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
            prop.addProperty("type", entry.getValue().equals("integer") ? "number" : "string");
            prop.addProperty("description", entry.getValue());
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
    public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
        try {
            // Convert JsonObject to Map for easier handling in executor if needed, 
            // or just pass the parameters as is. Our OpenFdaTools uses Map<String, Object>.
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            for (String key : parameters.keySet()) {
                com.google.gson.JsonElement element = parameters.get(key);
                if (element.isJsonPrimitive()) {
                    if (element.getAsJsonPrimitive().isNumber()) {
                        map.put(key, element.getAsDouble());
                    } else if (element.getAsJsonPrimitive().isBoolean()) {
                        map.put(key, element.getAsBoolean());
                    } else {
                        map.put(key, element.getAsString());
                    }
                }
            }
            
            String result = executor.apply(map);
            return JsonParser.parseString(result).getAsJsonObject();
        } catch (Exception e) {
            throw new ToolExecutionException("Tool execution failed: " + e.getMessage(), e);
        }
    }
}
