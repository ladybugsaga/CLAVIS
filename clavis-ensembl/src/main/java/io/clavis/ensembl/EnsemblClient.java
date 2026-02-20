package io.clavis.ensembl;

import io.clavis.core.http.HttpClientFactory;
import io.clavis.core.http.RateLimiter;
import io.clavis.core.http.RetryPolicy;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * Client for the Ensembl REST API.
 * Documentation: http://rest.ensembl.org/
 */
public class EnsemblClient {
    private static final Logger logger = LoggerFactory.getLogger(EnsemblClient.class);
    private static final String BASE_URL = "https://rest.ensembl.org/";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;

    public EnsemblClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(15); // Ensembl allows up to 15 req/s
        this.retryPolicy = RetryPolicy.defaultPolicy();
    }

    /**
     * Finds Ensembl identifiers for a given symbol (e.g., "BRCA2").
     */
    public String lookupSymbol(String symbol, String species) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "lookup/symbol/" + species + "/" + symbol))
                .newBuilder()
                .addQueryParameter("expand", "1")
                .build();
        return executeRequest(url);
    }

    /**
     * Retrieves details for an Ensembl identifier (e.g., "ENSG00000139618").
     */
    public String lookupId(String id) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "lookup/id/" + id))
                .newBuilder()
                .addQueryParameter("expand", "1")
                .build();
        return executeRequest(url);
    }

    /**
     * Retrieves genomic, cDNA, CDS, or protein sequence for an identifier.
     */
    public String getSequence(String id, String type) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "sequence/id/" + id))
                .newBuilder()
                .addQueryParameter("type", type != null ? type : "genomic")
                .build();
        return executeRequest(url);
    }

    /**
     * Retrieves genomic features (e.g., variations) overlapping a region or identifier.
     */
    public String getOverlap(String id, String feature) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "overlap/id/" + id))
                .newBuilder()
                .addQueryParameter("feature", feature != null ? feature : "variation")
                .build();
        return executeRequest(url);
    }

    /**
     * Retrieves Variant Effect Predictor (VEP) consequences for a specific HGVS or variant.
     */
    public String getVEP(String variant, String species) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "vep/" + species + "/hgvs/" + variant));
        return executeRequest(url);
    }

    private String executeRequest(HttpUrl url) throws IOException {
        try {
            return retryPolicy.execute(() -> {
                rateLimiter.acquire();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .build();
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Ensembl API error: " + response.code() + " " + response.message());
                    }
                    return Objects.requireNonNull(response.body()).string();
                }
            });
        } catch (io.clavis.core.exception.ApiException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
