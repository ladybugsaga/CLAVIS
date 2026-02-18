package io.clavis.intact;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import io.clavis.core.mcp.MCPTool;
import io.clavis.core.mcp.ToolExecutionException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class IntActTool implements MCPTool {
    private final String name;
    private final String description;
    private final Map<String, String> parameters;
    private final List<String> required;
    private final Function<Map<String, Object>, String> executor;

    public IntActTool(String name, String description, Map<String, String> parameters, List<String> required, Function<Map<String, Object>, String> executor) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
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
        
        JsonObject properties = new JsonObject();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            JsonObject prop = new JsonObject();
            prop.addProperty("type", "string"); // Simplifying to string for now
            prop.addProperty("description", entry.getValue());
            properties.add(entry.getKey(), prop);
        }
        schema.add("properties", properties);

        JsonArray requiredArray = new JsonArray();
        for (String req : required) {
            requiredArray.add(req);
        }
        schema.add("required", requiredArray);

        return schema;
    }

    @Override
    public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
        try {
            Map<String, Object> map = new java.util.HashMap<>();
            for (String key : parameters.keySet()) {
                if (parameters.get(key).isJsonPrimitive()) {
                    if (parameters.get(key).getAsJsonPrimitive().isNumber()) {
                        map.put(key, parameters.get(key).getAsDouble());
                    } else if (parameters.get(key).getAsJsonPrimitive().isBoolean()) {
                        map.put(key, parameters.get(key).getAsBoolean());
                    } else {
                        map.put(key, parameters.get(key).getAsString());
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
