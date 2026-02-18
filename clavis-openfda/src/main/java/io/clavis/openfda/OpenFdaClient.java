package io.clavis.openfda;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.clavis.core.http.HttpClientFactory;
import io.clavis.core.http.RateLimiter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Client for OpenFDA API.
 * 
 * Reports for drugs:
 * - Adverse events (/drug/event.json)
 * - Labels (/drug/label.json)
 * - Recalls/Enforcement (/drug/enforcement.json)
 */
public class OpenFdaClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenFdaClient.class);
    private static final String BASE_URL = "https://api.fda.gov";
    
    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final String apiKey;

    public OpenFdaClient(String apiKey) {
        this.httpClient = HttpClientFactory.createDefault();
        // openFDA without API key is 240 req/min (4 req/sec)
        // With API key is 2400 req/min (40 req/sec)
        int rate = (apiKey == null || apiKey.isEmpty()) ? 4 : 40;
        this.rateLimiter = new RateLimiter(rate);
        this.apiKey = apiKey;
        
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("No OpenFDA API key provided. Using restricted rate limit: {} req/sec", rate);
        }
    }

    public JsonObject searchEvents(String query, int limit) throws IOException {
        return executeSearch("/drug/event.json", query, limit);
    }

    public JsonObject searchLabels(String query, int limit) throws IOException {
        return executeSearch("/drug/label.json", query, limit);
    }

    public JsonObject searchEnforcements(String query, int limit) throws IOException {
        return executeSearch("/drug/enforcement.json", query, limit);
    }

    private JsonObject executeSearch(String endpoint, String query, int limit) throws IOException {
        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for rate limiter", e);
        }

        String url = BASE_URL + endpoint + "?search=" + query + "&limit=" + limit;
        if (apiKey != null && !apiKey.isEmpty()) {
            url += "&api_key=" + apiKey;
        }

        logger.debug("Executing OpenFDA query: {}", url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                logger.error("OpenFDA API error ({}): {}", response.code(), errorBody);
                throw new IOException("Unexpected response code " + response.code() + ": " + errorBody);
            }

            return JsonParser.parseString(response.body().string()).getAsJsonObject();
        }
    }
}
