package io.clavis.dailymed;

import io.clavis.core.mcp.MCPTool;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.List;
import java.util.function.Function;

public class DailyMedTool implements MCPTool {
    private final String name;
    private final String description;
    private final Map<String, String> propertyDescriptions;
    private final List<String> requiredProperties;
    private final Function<Map<String, Object>, String> executor;

    public DailyMedTool(String name, String description, Map<String, String> propertyDescriptions, 
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
            prop.addProperty("type", "string"); // Simplification
            prop.addProperty("description", entry.getValue());
            properties.add(entry.getKey(), prop);
        }
        schema.add("properties", properties);
        
        com.google.gson.JsonArray required = new com.google.gson.JsonArray();
        for (String req : requiredProperties) {
            required.add(req);
        }
        schema.add("required", required);
        
        return schema;
    }

    @Override
    public JsonObject execute(JsonObject parameters) {
        Map<String, Object> args = new java.util.HashMap<>();
        parameters.entrySet().forEach(entry -> {
            if (entry.getValue().isJsonPrimitive()) {
                if (entry.getValue().getAsJsonPrimitive().isNumber()) {
                    args.put(entry.getKey(), entry.getValue().getAsNumber());
                } else if (entry.getValue().getAsJsonPrimitive().isBoolean()) {
                    args.put(entry.getKey(), entry.getValue().getAsBoolean());
                } else {
                    args.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        });
        String result = executor.apply(args);
        return com.google.gson.JsonParser.parseString(result).getAsJsonObject();
    }
}
