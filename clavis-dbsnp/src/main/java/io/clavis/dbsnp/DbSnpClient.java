package io.clavis.dbsnp;

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
 * Client for the NCBI Variation Services API (dbSNP).
 * API: https://api.ncbi.nlm.nih.gov/variation/v0
 */
public class DbSnpClient {
    private static final String VARIATION_API = "https://api.ncbi.nlm.nih.gov/variation/v0";
    private static final String EUTILS_API = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;

    public DbSnpClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(3); // 3 req/s without API key
        this.retryPolicy = RetryPolicy.defaultPolicy();
    }

    /**
     * Get full RefSNP data by rsID.
     *
     * @param rsId numeric rsID (e.g. "7412" for rs7412)
     */
    public String getRefSnp(String rsId) throws IOException {
        // Strip "rs" prefix if present
        String numericId = rsId.toLowerCase().startsWith("rs") ? rsId.substring(2) : rsId;
        HttpUrl url = Objects.requireNonNull(
                HttpUrl.parse(VARIATION_API + "/refsnp/" + numericId));
        return executeRequest(url);
    }

    /**
     * Search for SNPs in a gene using NCBI E-utilities.
     *
     * @param gene   gene symbol (e.g. "BRCA1")
     * @param maxResults max results
     */
    public String searchByGene(String gene, int maxResults) throws IOException {
        // Step 1: search dbSNP for SNPs associated with a gene
        HttpUrl searchUrl = Objects.requireNonNull(HttpUrl.parse(EUTILS_API + "/esearch.fcgi"))
                .newBuilder()
                .addQueryParameter("db", "snp")
                .addQueryParameter("term", gene + "[gene]")
                .addQueryParameter("retmax", String.valueOf(maxResults))
                .addQueryParameter("retmode", "json")
                .build();
        return executeRequest(searchUrl);
    }

    /**
     * Get SNP summary data from E-utilities.
     *
     * @param rsIds comma-separated rsIDs (numeric)
     */
    public String getSummary(String rsIds) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(EUTILS_API + "/esummary.fcgi"))
                .newBuilder()
                .addQueryParameter("db", "snp")
                .addQueryParameter("id", rsIds)
                .addQueryParameter("retmode", "json")
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
                        throw new IOException("dbSNP API error: " + response.code() + " " + response.message());
                    }
                    return Objects.requireNonNull(response.body()).string();
                }
            });
        } catch (io.clavis.core.exception.ApiException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
