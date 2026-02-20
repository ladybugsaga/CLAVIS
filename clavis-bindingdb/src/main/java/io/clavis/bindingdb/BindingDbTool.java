package io.clavis.bindingdb;

import io.clavis.core.mcp.MCPTool;
import io.clavis.core.mcp.ToolExecutionException;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.function.Function;

public class BindingDbTool implements MCPTool {
    private final String name;
    private final String description;
    private final Map<String, String> propertyDescriptions;
    private final List<String> requiredProperties;
    private final Function<Map<String, Object>, String> executor;

    public BindingDbTool(String name, String description, Map<String, String> propertyDescriptions, 
                        List<String> requiredProperties, Function<Map<String, Object>, String> executor) {
        this.name = name;
        this.description = description;
        this.propertyDescriptions = propertyDescriptions;
        this.requiredProperties = requiredProperties;
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
        for (Map.Entry<String, String> entry : propertyDescriptions.entrySet()) {
            JsonObject prop = new JsonObject();
            prop.addProperty("type", "string");
            prop.addProperty("description", entry.getValue());
            properties.add(entry.getKey(), prop);
        }
        schema.add("properties", properties);
        
        JsonArray required = new JsonArray();
        for (String req : requiredProperties) {
            required.add(req);
        }
        schema.add("required", required);
        
        return schema;
    }

    @Override
    public JsonObject execute(JsonObject parameters) throws ToolExecutionException {
        Map<String, Object> args = new HashMap<>();
        parameters.entrySet().forEach(entry -> {
            if (entry.getValue().isJsonPrimitive()) {
                args.put(entry.getKey(), entry.getValue().getAsString());
            }
        });
        
        try {
            String result = executor.apply(args);
            com.google.gson.JsonElement element = com.google.gson.JsonParser.parseString(result);
            JsonObject response = new JsonObject();
            if (element.isJsonArray()) {
                response.add("results", element.getAsJsonArray());
            } else {
                response = element.getAsJsonObject();
            }
            return response;
        } catch (Exception e) {
            throw new ToolExecutionException("Failed to execute BindingDB tool " + name, e);
        }
    }
}
