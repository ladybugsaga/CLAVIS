package io.clavis.dailymed;

import com.google.gson.Gson;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DailyMedClient {
    private static final Logger logger = LoggerFactory.getLogger(DailyMedClient.class);
    private static final String BASE_URL = "https://dailymed.nlm.nih.gov/dailymed/services/v2";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final Gson gson;

    public DailyMedClient() {
        this.httpClient = HttpClientFactory.createDefault();
        // DailyMed doesn't specify hard limits but suggests being "mindful"
        // 5 requests per second is a safe baseline.
        this.rateLimiter = new RateLimiter(5);
        this.gson = new Gson();
    }

    public JsonObject searchSpls(String query, int page, int pageSize) throws IOException {
        String endpoint = "/spls.json?drug_name=" + URLEncoder.encode(query, StandardCharsets.UTF_8) 
                        + "&page=" + page + "&pagesize=" + pageSize;
        return executeGet(endpoint);
    }

    public JsonObject getSplDetails(String setId) throws IOException {
        String endpoint = "/spls/" + setId + ".json";
        return executeGet(endpoint);
    }

    public JsonObject searchDrugNames(String name) throws IOException {
        String endpoint = "/drugnames.json?drug_name=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
        return executeGet(endpoint);
    }

    public JsonObject getDrugClasses(String drugName) throws IOException {
        String endpoint = "/drugclasses.json?drug_name=" + URLEncoder.encode(drugName, StandardCharsets.UTF_8);
        return executeGet(endpoint);
    }

    public JsonObject getNdcsBySetId(String setId) throws IOException {
        String endpoint = "/spls/" + setId + "/ndcs.json";
        return executeGet(endpoint);
    }

    private JsonObject executeGet(String endpoint) throws IOException {
        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for rate limiter", e);
        }

        String url = BASE_URL + endpoint;
        logger.debug("Executing DailyMed query: {}", url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                logger.error("DailyMed API error ({}): {}", response.code(), errorBody);
                throw new IOException("Unexpected response code " + response.code() + ": " + errorBody);
            }

            String body = response.body().string();
            return JsonParser.parseString(body).getAsJsonObject();
        }
    }
}
