package io.clavis.alphafold;

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
 * Client for the AlphaFold Protein Structure Database API.
 * API: https://alphafold.ebi.ac.uk/api
 */
public class AlphaFoldClient {
    private static final String BASE_URL = "https://alphafold.ebi.ac.uk/api";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;

    public AlphaFoldClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(5);
        this.retryPolicy = RetryPolicy.defaultPolicy();
    }

    /**
     * Get structure prediction for a UniProt accession.
     *
     * @param uniprotId UniProt accession (e.g. "P04637" for TP53)
     */
    public String getPrediction(String uniprotId) throws IOException {
        HttpUrl url = Objects.requireNonNull(
                HttpUrl.parse(BASE_URL + "/prediction/" + uniprotId));
        return executeRequest(url);
    }

    /**
     * Search AlphaFold by UniProt accession — returns summary info.
     *
     * @param uniprotId UniProt accession
     */
    public String getUniProtSummary(String uniprotId) throws IOException {
        HttpUrl url = Objects.requireNonNull(
                HttpUrl.parse(BASE_URL + "/uniprot/summary/" + uniprotId + ".json"));
        return executeRequest(url);
    }

    /**
     * Get available annotations for a structure.
     *
     * @param qualifier the qualifier/accession (e.g. "AF-P04637-F1")
     */
    public String getAnnotations(String qualifier) throws IOException {
        HttpUrl url = Objects.requireNonNull(
                HttpUrl.parse(BASE_URL + "/annotations/" + qualifier));
        return executeRequest(url);
    }

    /**
     * Get the URL for PDB file download.
     *
     * @param entryId AlphaFold entry ID (e.g. "AF-P04637-F1")
     */
    public String getPdbUrl(String entryId) {
        return "https://alphafold.ebi.ac.uk/files/" + entryId + "-model_v4.pdb";
    }

    /**
     * Get the URL for mmCIF file download.
     *
     * @param entryId AlphaFold entry ID (e.g. "AF-P04637-F1")
     */
    public String getCifUrl(String entryId) {
        return "https://alphafold.ebi.ac.uk/files/" + entryId + "-model_v4.cif";
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
                        throw new IOException("AlphaFold API error: " + response.code() + " " + response.message());
                    }
                    return Objects.requireNonNull(response.body()).string();
                }
            });
        } catch (io.clavis.core.exception.ApiException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
