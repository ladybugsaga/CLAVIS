package io.clavis.gtex;

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
 * Client for GTEx Portal API (v2).
 */
public class GtexClient {
    private static final Logger logger = LoggerFactory.getLogger(GtexClient.class);
    private static final String BASE_URL = "https://gtexportal.org/api/v2/";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;

    public GtexClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(5); // 5 req/s for GTEx
        this.retryPolicy = RetryPolicy.defaultPolicy();
    }

    /**
     * Retrieves median gene expression data.
     */
    public String getMedianGeneExpression(String geneId, String tissueSiteDetailId) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "expression/medianGeneExpression"))
                .newBuilder()
                .addQueryParameter("geneId", geneId);
        if (tissueSiteDetailId != null && !tissueSiteDetailId.isEmpty()) {
            urlBuilder.addQueryParameter("tissueSiteDetailId", tissueSiteDetailId);
        }
        return executeRequest(urlBuilder.build());
    }

    /**
     * Finds top expressed genes for a specfied tissue.
     */
    public String getTopExpressedGenes(String tissueSiteDetailId) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "expression/topExpressedGene"))
                .newBuilder()
                .addQueryParameter("tissueSiteDetailId", tissueSiteDetailId)
                .build();
        return executeRequest(url);
    }

    /**
     * Finds eGenes for specific genes and tissues.
     */
    public String getEgenes(String geneId, String tissueSiteDetailId) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "association/egene"))
                .newBuilder();
        if (geneId != null && !geneId.isEmpty()) {
            urlBuilder.addQueryParameter("geneId", geneId);
        }
        if (tissueSiteDetailId != null && !tissueSiteDetailId.isEmpty()) {
            urlBuilder.addQueryParameter("tissueSiteDetailId", tissueSiteDetailId);
        }
        return executeRequest(urlBuilder.build());
    }

    /**
     * Retrieves significant single-tissue eQTLs.
     */
    public String getSingleTissueEqtls(String geneId, String tissueSiteDetailId) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "association/singleTissueEqtl"))
                .newBuilder();
        if (geneId != null && !geneId.isEmpty()) {
            urlBuilder.addQueryParameter("geneId", geneId);
        }
        if (tissueSiteDetailId != null && !tissueSiteDetailId.isEmpty()) {
            urlBuilder.addQueryParameter("tissueSiteDetailId", tissueSiteDetailId);
        }
        return executeRequest(urlBuilder.build());
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
                        throw new IOException("GTEx API error: " + response.code() + " " + response.message());
                    }
                    return Objects.requireNonNull(response.body()).string();
                }
            });
        } catch (io.clavis.core.exception.ApiException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
