package io.clavis.pharmvar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.clavis.core.exception.ApiException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PharmVarClient {
    private static final Logger logger = LoggerFactory.getLogger(PharmVarClient.class);
    private static final String BASE_URL = "https://www.pharmvar.org/api-service/";
    
    private final OkHttpClient httpClient;
    private final Gson gson;

    public PharmVarClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * Lists all genes available in PharmVar.
     */
    public JsonArray listGenes() throws ApiException {
        return executeGetArray("genes/list");
    }

    /**
     * Retrieves details for a specific gene by symbol.
     */
    public JsonArray getGene(String symbol) throws ApiException {
        return executeGetArray("genes/" + symbol);
    }

    /**
     * Lists all active alleles in PharmVar.
     */
    public JsonArray listAlleles() throws ApiException {
        return executeGetArray("alleles/list");
    }

    /**
     * Retrieves details for a specific allele by PharmVar ID or allele name.
     */
    public JsonArray getAllele(String identifier) throws ApiException {
        return executeGetArray("alleles/" + identifier);
    }

    /**
     * Retrieves the CPIC Clinical Function for a given allele.
     */
    public String getAlleleFunction(String identifier) throws ApiException {
        Request request = new Request.Builder()
                .url(BASE_URL + "alleles/" + identifier + "/function")
                .addHeader("Accept", "text/plain")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("PharmVar API error: " + response.code() + " " + response.message());
            }
            return response.body().string();
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with PharmVar", e);
        }
    }

    private JsonArray executeGetArray(String endpoint) throws ApiException {
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("PharmVar API error: " + response.code() + " " + response.message());
            }
            String body = response.body().string();
            return gson.fromJson(body, JsonArray.class);
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with PharmVar", e);
        }
    }
}
