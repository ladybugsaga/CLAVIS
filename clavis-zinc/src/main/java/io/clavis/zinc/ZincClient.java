package io.clavis.zinc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import io.clavis.core.exception.ApiException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ZincClient {
    private static final Logger logger = LoggerFactory.getLogger(ZincClient.class);
    private static final String BASE_URL = "https://zinc15.docking.org/";
    
    private final OkHttpClient httpClient;
    private final Gson gson;

    public ZincClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public JsonArray searchSubstances(String query) throws ApiException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "substances.json").newBuilder()
                .addQueryParameter("q", query)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("ZINC API error: " + response.code() + " " + response.message());
            }
            String body = response.body().string();
            // ZINC search returns an array of substances
            return gson.fromJson(body, JsonArray.class);
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with ZINC", e);
        }
    }

    public JsonObject getSubstanceDetails(String zincId) throws ApiException {
        // Ensure ZINC ID is in correct format if needed, but usually it's ZINCxxxxxxxx
        HttpUrl url = HttpUrl.parse(BASE_URL + "substances/" + zincId + ".json");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("ZINC API error: " + response.code() + " " + response.message());
            }
            String body = response.body().string();
            return gson.fromJson(body, JsonObject.class);
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with ZINC", e);
        }
    }
}
