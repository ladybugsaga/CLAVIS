package io.clavis.kegg;

import io.clavis.core.http.HttpClientFactory;
import io.clavis.core.http.RateLimiter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * REST client for the KEGG (Kyoto Encyclopedia of Genes and Genomes) API.
 * Base URL: https://rest.kegg.jp
 * Returns plain text (tab-delimited for lists, flat-file for entries).
 */
public class KEGGClient {

    private static final Logger logger = LoggerFactory.getLogger(KEGGClient.class);
    private static final String API_BASE = "https://rest.kegg.jp";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;

    public KEGGClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(3); // KEGG limit: 3 req/s
    }

    /** Search pathways by keyword. Returns tab-delimited text. */
    public String findPathways(String query) throws IOException {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return executeRequest(API_BASE + "/find/pathway/" + encoded);
    }

    /** Get detailed entry by KEGG ID (pathway, compound, disease, drug, gene). */
    public String getEntry(String keggId) throws IOException {
        return executeRequest(API_BASE + "/get/" + keggId);
    }

    /** Search genes by keyword across all organisms. */
    public String findGenes(String query) throws IOException {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return executeRequest(API_BASE + "/find/genes/" + encoded);
    }

    /** Find pathways linked to a gene ID (e.g. hsa:7157 for TP53). */
    public String getLinkedPathways(String geneId) throws IOException {
        return executeRequest(API_BASE + "/link/pathway/" + geneId);
    }

    /** Search compounds by keyword. */
    public String findCompounds(String query) throws IOException {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return executeRequest(API_BASE + "/find/compound/" + encoded);
    }

    private String executeRequest(String url) throws IOException {
        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
        logger.debug("KEGG request: {}", url);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept-Encoding", "identity")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("KEGG API error " + response.code() + ": " + response.message());
            }
            String body = response.body() != null ? response.body().string() : "";
            return body;
        }
    }
}
