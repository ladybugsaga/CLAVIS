package io.clavis.intact;

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

public class IntActClient {
    private static final Logger logger = LoggerFactory.getLogger(IntActClient.class);
    private static final String BASE_URL = "https://www.ebi.ac.uk/intact/ws/interaction";
    
    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;

    public IntActClient() {
        this.httpClient = HttpClientFactory.createDefault();
        // IntAct doesn't specify a strict rate limit for public access, but 5 req/s is safe
        this.rateLimiter = new RateLimiter(5);
    }

    public JsonObject searchInteractions(String query, int page, int pageSize) throws IOException {
        String url = String.format("%s/findInteractions/%s?page=%d&pageSize=%d", BASE_URL, query, page, pageSize);
        return executeRequest(url);
    }

    public JsonObject getInteractorsList(String query, int page, int pageSize) throws IOException {
        // IntAct also has an interactors search
        String url = String.format("%s/interactors/list?query=%s&page=%d&pageSize=%d", BASE_URL, query, page, pageSize);
        return executeRequest(url);
    }

    private JsonObject executeRequest(String url) throws IOException {
        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for rate limiter", e);
        }

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + " " + response.message());
            }

            String body = response.body().string();
            return JsonParser.parseString(body).getAsJsonObject();
        }
    }
}
