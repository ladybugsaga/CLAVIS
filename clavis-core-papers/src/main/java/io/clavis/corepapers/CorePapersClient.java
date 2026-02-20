package io.clavis.corepapers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.clavis.core.exception.ApiException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CorePapersClient {
    private static final Logger logger = LoggerFactory.getLogger(CorePapersClient.class);
    private static final String BASE_URL = "https://api.core.ac.uk/v3/";
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;

    public CorePapersClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        // Check for API key in environment
        String key = System.getenv("CORE_API_KEY");
        this.apiKey = (key != null && !key.isEmpty()) ? key : null;
        
        if (apiKey == null) {
            logger.warn("CORE_API_KEY not found. API calls may be limited or fail.");
        }
    }

    private Request.Builder authenticatedRequestBuilder(String endpoint) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + endpoint).newBuilder();
        if (apiKey != null) {
            urlBuilder.addQueryParameter("api_key", apiKey);
        }
        
        return new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Accept", "application/json");
    }

    public JsonObject searchPapers(String query, int limit) throws ApiException {
        // CORE v3 search uses POST for complex queries, but simple GET works for basic search
        HttpUrl url = HttpUrl.parse(BASE_URL + "search/outputs").newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("limit", String.valueOf(limit))
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json");
        
        if (apiKey != null) {
            url = url.newBuilder().addQueryParameter("api_key", apiKey).build();
            requestBuilder.url(url);
        }

        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("CORE API error: " + response.code() + " " + response.message());
            }
            String body = response.body().string();
            return gson.fromJson(body, JsonObject.class);
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with CORE", e);
        }
    }

    public JsonObject getPaperDetails(String coreId) throws ApiException {
        Request.Builder requestBuilder = authenticatedRequestBuilder("outputs/" + coreId);

        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("CORE API error: " + response.code() + " " + response.message());
            }
            String body = response.body().string();
            return gson.fromJson(body, JsonObject.class);
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with CORE", e);
        }
    }
}
