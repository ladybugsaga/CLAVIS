package io.clavis.rxnorm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.clavis.core.exception.ApiException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RxNormClient {
    private static final Logger logger = LoggerFactory.getLogger(RxNormClient.class);
    private static final String BASE_URL = "https://rxnav.nlm.nih.gov/REST/";
    
    private final OkHttpClient httpClient;
    private final Gson gson;

    public RxNormClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public JsonObject search(String name) throws ApiException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "drugs.json")
                .newBuilder()
                .addQueryParameter("name", name)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("RxNav API error: " + response.code() + " " + response.message());
            }
            String body = response.body().string();
            return gson.fromJson(body, JsonObject.class);
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with RxNav", e);
        }
    }

    public JsonObject getRxcui(String name) throws ApiException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "rxcui.json")
                .newBuilder()
                .addQueryParameter("name", name)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("RxNav API error: " + response.code() + " " + response.message());
            }
            String body = response.body().string();
            return gson.fromJson(body, JsonObject.class);
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with RxNav", e);
        }
    }

    public JsonObject getAllProperties(String rxcui) throws ApiException {
        String url = BASE_URL + "rxcui/" + rxcui + "/allProperties.json?propName=all";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("RxNav API error: " + response.code() + " " + response.message());
            }
            String body = response.body().string();
            return gson.fromJson(body, JsonObject.class);
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with RxNav", e);
        }
    }
}
