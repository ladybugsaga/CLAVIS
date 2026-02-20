package io.clavis.ctd;

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
 * Client for CTD data via BioThings APIs (MyChem.info and MyGene.info).
 */
public class CtdClient {
    private static final Logger logger = LoggerFactory.getLogger(CtdClient.class);
    private static final String MYCHEM_BASE = "https://mychem.info/v1/";
    private static final String MYGENE_BASE = "https://mygene.info/v1/";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;

    public CtdClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(10); // 10 req/s for BioThings
        this.retryPolicy = RetryPolicy.defaultPolicy();
    }

    /**
     * Retrieves curated CTD interactions for a chemical using MyChem.info.
     */
    public String getChemicalInteractions(String chemical) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(MYCHEM_BASE + "query"))
                .newBuilder()
                .addQueryParameter("q", chemical)
                .addQueryParameter("fields", "ctd")
                .build();
        return executeRequest(url);
    }

    /**
     * Retrieves curated CTD interactions for a gene using MyGene.info.
     */
    public String getGeneInteractions(String gene) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(MYGENE_BASE + "query"))
                .newBuilder()
                .addQueryParameter("q", gene)
                .addQueryParameter("fields", "ctd")
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
                        throw new IOException("BioThings API error: " + response.code() + " " + response.message());
                    }
                    return Objects.requireNonNull(response.body()).string();
                }
            });
        } catch (io.clavis.core.exception.ApiException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
