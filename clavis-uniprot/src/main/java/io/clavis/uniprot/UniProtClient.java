package io.clavis.uniprot;

import io.clavis.core.exception.ApiException;
import io.clavis.core.http.HttpClientFactory;
import io.clavis.core.http.RateLimiter;
import io.clavis.core.http.RetryPolicy;
import io.clavis.core.logging.StructuredLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Client for the UniProt REST API.
 *
 * <p>
 * Accesses the UniProtKB database of 250M+ protein entries.
 * No API key required. Rate limited to 1 request/second.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 * @see <a href="https://rest.uniprot.org">UniProt REST API</a>
 */
public class UniProtClient {

    private static final String API_BASE = "https://rest.uniprot.org";

    private final StructuredLogger logger;
    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;

    public UniProtClient() {
        this.logger = new StructuredLogger(UniProtClient.class);
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(1);
        this.retryPolicy = RetryPolicy.defaultPolicy();
        logger.info("UniProt client initialized. Rate limit: 1 req/s (no key required).");
    }

    /**
     * Searches UniProtKB for proteins matching the query.
     *
     * @param query      search query (UniProt query syntax)
     * @param maxResults max results (1-500)
     * @param organism   optional organism filter (e.g. "9606" for human)
     * @param reviewed   optional filter for reviewed (Swiss-Prot) entries only
     * @return JSON response string
     * @throws ApiException if the request fails
     */
    public String searchProteins(String query, int maxResults, String organism, Boolean reviewed) throws ApiException {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }
        return retryPolicy.execute(() -> {
            StringBuilder url = new StringBuilder(API_BASE).append("/uniprotkb/search?");

            StringBuilder q = new StringBuilder(query);
            if (organism != null && !organism.isEmpty()) {
                q.append(" AND organism_id:").append(organism);
            }
            if (reviewed != null && reviewed) {
                q.append(" AND reviewed:true");
            }

            url.append("query=").append(urlEncode(q.toString()));
            url.append("&size=").append(Math.min(maxResults, 500));
            url.append("&format=json");
            url.append(
                    "&fields=accession,id,protein_name,gene_names,organism_name,length,cc_function,cc_subcellular_location,go_p,go_f");

            logger.info("[uniprot] search: query=" + query + ", limit=" + maxResults);
            return executeRequest(url.toString());
        });
    }

    /**
     * Gets a single protein entry by accession.
     *
     * @param accession UniProt accession (e.g. "P01308")
     * @return JSON response string
     * @throws ApiException if the request fails
     */
    public String getProtein(String accession) throws ApiException {
        return retryPolicy.execute(() -> {
            String url = API_BASE + "/uniprotkb/" + urlEncode(accession) + "?format=json";
            logger.info("[uniprot] get protein: accession=" + accession);
            return executeRequest(url);
        });
    }

    /**
     * Gets the FASTA sequence for a protein.
     *
     * @param accession UniProt accession
     * @return FASTA formatted sequence
     * @throws ApiException if the request fails
     */
    public String getSequence(String accession) throws ApiException {
        return retryPolicy.execute(() -> {
            String url = API_BASE + "/uniprotkb/" + urlEncode(accession) + "?format=fasta";
            logger.info("[uniprot] get sequence: accession=" + accession);
            return executeRequest(url);
        });
    }

    /**
     * Searches UniProt by gene name.
     *
     * @param geneName   gene name (e.g. "BRCA1")
     * @param organism   optional organism ID
     * @param maxResults maximum results
     * @return JSON response string
     * @throws ApiException if the request fails
     */
    public String searchByGene(String geneName, String organism, int maxResults) throws ApiException {
        String query = "gene:" + geneName;
        return searchProteins(query, maxResults, organism, true);
    }

    /**
     * Gets protein function annotation (comments of type FUNCTION).
     *
     * @param accession UniProt accession
     * @return JSON response string for the full protein entry
     * @throws ApiException if the request fails
     */
    public String getProteinFunction(String accession) throws ApiException {
        return getProtein(accession);
    }

    /**
     * Searches for proteins by taxonomy/organism.
     *
     * @param organism   organism name or taxonomy ID
     * @param keyword    optional keyword to filter
     * @param maxResults max results
     * @return JSON response string
     * @throws ApiException if the request fails
     */
    public String searchByOrganism(String organism, String keyword, int maxResults) throws ApiException {
        StringBuilder query = new StringBuilder("organism_name:\"" + organism + "\"");
        if (keyword != null && !keyword.isEmpty()) {
            query.append(" AND ").append(keyword);
        }
        return retryPolicy.execute(() -> {
            StringBuilder url = new StringBuilder(API_BASE).append("/uniprotkb/search?");
            url.append("query=").append(urlEncode(query.toString()));
            url.append("&size=").append(Math.min(maxResults, 500));
            url.append("&format=json");
            url.append("&fields=accession,id,protein_name,gene_names,organism_name,length,cc_function");

            logger.info("[uniprot] search by organism: " + organism);
            return executeRequest(url.toString());
        });
    }

    // ---- Internal helpers ----

    private String executeRequest(String url) throws IOException, InterruptedException {
        rateLimiter.acquire();
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Accept-Encoding", "identity")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No body";
                throw new IOException("UniProt API error " + response.code() + ": " + errorBody);
            }
            return response.body() != null ? response.body().string() : "";
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
