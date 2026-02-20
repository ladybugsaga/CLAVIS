package io.clavis.hmdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.clavis.core.exception.ApiException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HmdbClient {
    private static final Logger logger = LoggerFactory.getLogger(HmdbClient.class);
    private static final String BASE_URL = "https://hmdb.ca/metabolites/";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    
    private final OkHttpClient httpClient;
    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;
    private final Gson gson;

    public HmdbClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.xmlMapper = new XmlMapper();
        this.jsonMapper = new ObjectMapper();
        this.gson = new Gson();
    }

    public JsonObject getMetabolite(String hmdbId) throws ApiException {
        // Ensure ID format is correct (e.g., HMDB0000001)
        if (!hmdbId.toUpperCase().startsWith("HMDB")) {
             // Basic search simulation if a name is provided? 
             // HMDB doesn't have a simple REST search endpoint that returns JSON/XML easily without scraping.
             // We'll stick to ID-based retrieval for now as specified in the use case.
        }

        String url = BASE_URL + hmdbId.toUpperCase() + ".xml";
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/xml")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    throw new ApiException("Metabolite not found: " + hmdbId);
                }
                throw new ApiException("HMDB API error: " + response.code() + " " + response.message());
            }

            String xml = response.body().string();
            JsonNode node = xmlMapper.readTree(xml);
            String json = jsonMapper.writeValueAsString(node);
            
            return gson.fromJson(json, JsonObject.class);
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with HMDB", e);
        }
    }

    /**
     * Search is tricky for HMDB as they don't have a dedicated API.
     * We'll implement a simple text search by trying to resolve names to IDs or 
     * guiding the user to use IDs.
     */
    public JsonObject search(String query) throws ApiException {
        // For now, if it looks like an ID, get it. Otherwise, explain the ID format.
        if (query.toUpperCase().matches("HMDB\\d{7}")) {
            return getMetabolite(query);
        }
        
        JsonObject error = new JsonObject();
        error.addProperty("status", "error");
        error.addProperty("message", "HMDB tools currently require a valid HMDB ID (e.g., HMDB0000122 for Glucose). Programmatic text search is planned in a future update.");
        return error;
    }
}
