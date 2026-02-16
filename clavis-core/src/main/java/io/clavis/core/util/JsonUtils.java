package io.clavis.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility methods for JSON processing using Gson.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public final class JsonUtils {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Gson COMPACT_GSON = new Gson();

    private JsonUtils() {
        // Static utility class
    }

    /**
     * Converts an object to its JSON representation.
     *
     * @param obj the object to serialize
     * @return the JSON string
     */
    public static String toJson(Object obj) {
        return COMPACT_GSON.toJson(obj);
    }

    /**
     * Converts an object to a pretty-printed JSON string.
     *
     * @param obj the object to serialize
     * @return the pretty-printed JSON string
     */
    public static String toPrettyJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * Parses a JSON string into a JsonObject.
     *
     * @param json the JSON string
     * @return the parsed JsonObject
     */
    public static JsonObject parseObject(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    /**
     * Deserializes a JSON string into an object of the given class.
     *
     * @param <T>   the target type
     * @param json  the JSON string
     * @param clazz the target class
     * @return the deserialized object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return COMPACT_GSON.fromJson(json, clazz);
    }

    /**
     * Safely gets a string value from a JsonObject.
     *
     * @param obj          the JSON object
     * @param key          the key to look up
     * @param defaultValue default if missing
     * @return the string value or default
     */
    public static String getString(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return defaultValue;
    }

    /**
     * Safely gets an int value from a JsonObject.
     *
     * @param obj          the JSON object
     * @param key          the key to look up
     * @param defaultValue default if missing
     * @return the int value or default
     */
    public static int getInt(JsonObject obj, String key, int defaultValue) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsInt();
        }
        return defaultValue;
    }
}
