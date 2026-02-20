package io.clavis.clinvar;

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
 * Client for the NCBI ClinVar API using E-utilities.
 */
public class ClinVarClient {
    private static final Logger logger = LoggerFactory.getLogger(ClinVarClient.class);
    private static final String EUTILS_BASE = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;

    public ClinVarClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(3); // 3 req/s for E-utilities without API key
        this.retryPolicy = RetryPolicy.defaultPolicy();
    }

    /**
     * Search for ClinVar records using a query string.
     */
    public String search(String query, int maxResults) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(EUTILS_BASE + "esearch.fcgi"))
                .newBuilder()
                .addQueryParameter("db", "clinvar")
                .addQueryParameter("term", query)
                .addQueryParameter("retmax", String.valueOf(maxResults))
                .addQueryParameter("retmode", "json")
                .build();
        return executeRequest(url);
    }

    /**
     * Retrieve summary information for specific ClinVar UIDs.
     */
    public String getSummary(String uids) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(EUTILS_BASE + "esummary.fcgi"))
                .newBuilder()
                .addQueryParameter("db", "clinvar")
                .addQueryParameter("id", uids)
                .addQueryParameter("retmode", "json")
                .build();
        return executeRequest(url);
    }

    /**
     * Retrieve detailed records for specific ClinVar UIDs (XML format).
     */
    public String getDetails(String uids) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(EUTILS_BASE + "efetch.fcgi"))
                .newBuilder()
                .addQueryParameter("db", "clinvar")
                .addQueryParameter("id", uids)
                .addQueryParameter("retmode", "xml")
                .build();
        return executeRequest(url);
    }

    private String executeRequest(HttpUrl url) throws IOException {
        try {
            return retryPolicy.execute(() -> {
                rateLimiter.acquire();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("ClinVar API error: " + response.code() + " " + response.message());
                    }
                    return Objects.requireNonNull(response.body()).string();
                }
            });
        } catch (io.clavis.core.exception.ApiException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
