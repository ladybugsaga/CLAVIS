package io.clavis.bindingdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import io.clavis.core.exception.ApiException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BindingDbClient {
    private static final Logger logger = LoggerFactory.getLogger(BindingDbClient.class);
    private static final String BASE_URL = "https://bindingdb.org/rest/";
    
    private final OkHttpClient httpClient;
    private final Gson gson;

    public BindingDbClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * Retrieves ligands and their binding data for a given UniProt ID.
     * @param uniprot The UniProt ID (e.g., P35355)
     * @param cutoff Optional affinity cutoff in nM
     * @return JsonArray of binding data
     */
    public JsonArray getLigandsByUniprot(String uniprot, String cutoff) throws ApiException {
        String query = uniprot;
        if (cutoff != null && !cutoff.isEmpty()) {
            query += ";" + cutoff;
        }

        HttpUrl url = HttpUrl.parse(BASE_URL + "getLigandsByUniprot").newBuilder()
                .addQueryParameter("uniprot", query)
                .addQueryParameter("response", "application/json")
                .build();

        return executeRequest(url);
    }

    /**
     * Retrieves protein targets and affinities for a given compound SMILES.
     * @param smiles The compound SMILES string
     * @param similarity Optional similarity cutoff (0.0 to 1.0)
     * @return JsonArray of target data
     */
    public JsonArray getTargetByCompound(String smiles, String similarity) throws ApiException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "getTargetByCompound").newBuilder()
                .addQueryParameter("smiles", smiles)
                .addQueryParameter("response", "application/json");
        
        if (similarity != null && !similarity.isEmpty()) {
            urlBuilder.addQueryParameter("cutoff", similarity);
        }

        return executeRequest(urlBuilder.build());
    }

    private JsonArray executeRequest(HttpUrl url) throws ApiException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("BindingDB API error: " + response.code() + " " + response.message());
            }
            String body = response.body().string();
            // BindingDB sometimes returns a single object if there's only one result, 
            // but we want consistent Array output for tools.
            try {
                return gson.fromJson(body, JsonArray.class);
            } catch (Exception e) {
                // Try parsing as object and wrapping in array
                JsonArray arr = new JsonArray();
                arr.add(gson.fromJson(body, JsonObject.class));
                return arr;
            }
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with BindingDB", e);
        }
    }
}
