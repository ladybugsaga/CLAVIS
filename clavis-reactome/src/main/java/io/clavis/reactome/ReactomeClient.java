package io.clavis.reactome;

import io.clavis.core.http.HttpClientFactory;
import io.clavis.core.http.RateLimiter;
import io.clavis.core.http.RetryPolicy;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

/**
 * Client for the Reactome Content Service REST API.
 * Base URL: https://reactome.org/ContentService
 */
public class ReactomeClient {
    private static final String BASE_URL = "https://reactome.org/ContentService";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;

    public ReactomeClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(5);
        this.retryPolicy = RetryPolicy.defaultPolicy();
    }

    /**
     * Search Reactome for pathways, reactions, and entities.
     */
    public String search(String query, String species, int rows) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/search/query"))
                .newBuilder()
                .addQueryParameter("query", query)
                .addQueryParameter("species", species != null ? species : "Homo sapiens")
                .addQueryParameter("cluster", "true")
                .addQueryParameter("rows", String.valueOf(rows))
                .build();
        return executeRequest(url);
    }

    /**
     * Get detailed information about a pathway or reaction by stable ID.
     *
     * @param stableId Reactome stable ID (e.g. "R-HSA-1640170")
     */
    public String getById(String stableId) throws IOException {
        HttpUrl url = Objects.requireNonNull(
                HttpUrl.parse(BASE_URL + "/data/query/" + stableId));
        return executeRequest(url);
    }

    /**
     * Get participating physical entities of a pathway or reaction.
     *
     * @param pathwayId Reactome stable ID
     */
    public String getParticipants(String pathwayId) throws IOException {
        HttpUrl url = Objects.requireNonNull(
                HttpUrl.parse(BASE_URL + "/data/participants/" + pathwayId));
        return executeRequest(url);
    }

    /**
     * Get pathways that contain a given entity (gene, protein, compound).
     * Uses the low-level entity endpoint.
     *
     * @param entityId Gene name, UniProt ID, ChEBI ID, etc.
     * @param species  species name (default: Homo sapiens)
     */
    public String getPathwaysForEntity(String entityId, String species) throws IOException {
        HttpUrl url = Objects.requireNonNull(
                HttpUrl.parse(BASE_URL + "/data/pathways/low/entity/" + entityId))
                .newBuilder()
                .addQueryParameter("species", species != null ? species : "Homo sapiens")
                .build();
        return executeRequest(url);
    }

    private String executeRequest(HttpUrl url) throws IOException {
        try {
            return retryPolicy.execute(() -> {
                rateLimiter.acquire();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Accept", "application/json")
                        .build();
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Reactome API error: " + response.code() + " " + response.message());
                    }
                    return Objects.requireNonNull(response.body()).string();
                }
            });
        } catch (io.clavis.core.exception.ApiException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
