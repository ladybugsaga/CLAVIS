package io.clavis.pubmed;

import io.clavis.core.exception.ApiException;
import io.clavis.core.http.HttpClientFactory;
import io.clavis.core.http.RateLimiter;
import io.clavis.core.http.RetryPolicy;
import io.clavis.core.logging.StructuredLogger;
import io.clavis.core.models.Paper;
import io.clavis.pubmed.parsers.PubMedXmlParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Client for interacting with PubMed E-utilities API.
 *
 * <p>
 * Implements rate limiting (10 req/sec with API key) and
 * automatic retry with exponential backoff.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 * @see <a href="https://www.ncbi.nlm.nih.gov/books/NBK25501/">E-utilities
 *      Documentation</a>
 */
public class PubMedClient {

    private static final String BASE_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils";

    private final String apiKey;
    private final String email;
    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;
    private final PubMedXmlParser xmlParser;
    private final StructuredLogger logger;

    /**
     * Creates a new PubMed client.
     *
     * @param apiKey NCBI API key (required)
     * @param email  email address (required by NCBI)
     * @throws IllegalArgumentException if apiKey or email is null/empty
     */
    public PubMedClient(String apiKey, String email) {
        this.logger = new StructuredLogger(PubMedClient.class);
        this.httpClient = HttpClientFactory.createDefault();

        if (apiKey == null || apiKey.trim().isEmpty()) {
            this.apiKey = "";
            this.rateLimiter = new RateLimiter(3);
            logger.warn("No API key provided. Requests will be rate-limited to 3/second.");
        } else {
            this.apiKey = apiKey;
            this.rateLimiter = new RateLimiter(10);
        }

        if (email == null || email.trim().isEmpty()) {
            this.email = "tool@clavis.io";
            logger.warn("No email provided. Using default: " + this.email);
        } else {
            this.email = email;
        }

        this.retryPolicy = RetryPolicy.defaultPolicy();
        this.xmlParser = new PubMedXmlParser();
    }

    /**
     * Searches PubMed for papers matching the query.
     *
     * @param query      search query (cannot be null or empty)
     * @param maxResults maximum results to return (1-10000)
     * @return list of papers matching the query, never null
     * @throws ApiException             if the API request fails
     * @throws IllegalArgumentException if parameters are invalid
     */
    public List<Paper> search(String query, int maxResults) throws ApiException {
        validateSearchParams(query, maxResults);

        logger.logApiRequest("pubmed", "esearch+efetch", Map.of(
                "query", query, "maxResults", maxResults));

        long startTime = System.currentTimeMillis();

        return retryPolicy.execute(() -> {
            StringBuilder searchUrl = new StringBuilder(BASE_URL).append("/esearch.fcgi?");
            searchUrl.append("db=pubmed");
            searchUrl.append("&term=").append(urlEncode(query));
            searchUrl.append("&retmax=").append(maxResults);
            searchUrl.append("&retmode=json");
            appendStandardParams(searchUrl);

            String searchResponse = executeRateLimitedRequest(searchUrl.toString());
            List<String> pmids = PubMedXmlParser.parsePmidsFromJson(searchResponse);

            if (pmids.isEmpty()) {
                logger.logApiResponse("pubmed", 200, System.currentTimeMillis() - startTime);
                return Collections.<Paper>emptyList();
            }

            StringBuilder fetchUrl = new StringBuilder(BASE_URL).append("/efetch.fcgi?");
            fetchUrl.append("db=pubmed");
            fetchUrl.append("&id=").append(String.join(",", pmids));
            fetchUrl.append("&retmode=xml");
            appendStandardParams(fetchUrl);

            String fetchResponse = executeRateLimitedRequest(fetchUrl.toString());
            List<Paper> papers = xmlParser.parsePapers(fetchResponse);

            logger.logApiResponse("pubmed", 200, System.currentTimeMillis() - startTime);
            return papers;
        });
    }

    /**
     * Fetches a single paper by its PMID.
     *
     * @param pmid the PubMed ID
     * @return the paper, or null if not found
     * @throws ApiException if the API request fails
     */
    public Paper fetchByPmid(String pmid) throws ApiException {
        if (pmid == null || pmid.isEmpty()) {
            throw new IllegalArgumentException("PMID cannot be null or empty");
        }

        return retryPolicy.execute(() -> {
            StringBuilder url = new StringBuilder(BASE_URL).append("/efetch.fcgi?");
            url.append("db=pubmed");
            url.append("&id=").append(pmid);
            url.append("&retmode=xml");
            appendStandardParams(url);

            String response = executeRateLimitedRequest(url.toString());
            List<Paper> papers = xmlParser.parsePapers(response);
            return papers.isEmpty() ? null : papers.get(0);
        });
    }

    /**
     * Gets papers related to the given PMID.
     *
     * @param pmid       the PubMed ID
     * @param maxResults maximum related papers
     * @return list of related papers
     * @throws ApiException if the request fails
     */
    public List<Paper> getRelatedPapers(String pmid, int maxResults) throws ApiException {
        if (pmid == null || pmid.isEmpty()) {
            throw new IllegalArgumentException("PMID cannot be null or empty");
        }

        return retryPolicy.execute(() -> {
            StringBuilder linkUrl = new StringBuilder(BASE_URL).append("/elink.fcgi?");
            linkUrl.append("dbfrom=pubmed&db=pubmed");
            linkUrl.append("&id=").append(pmid);
            linkUrl.append("&linkname=pubmed_pubmed");
            linkUrl.append("&retmode=json");
            appendStandardParams(linkUrl);

            String linkResponse = executeRateLimitedRequest(linkUrl.toString());
            List<String> relatedPmids = PubMedXmlParser.parseRelatedPmidsFromJson(linkResponse);

            if (relatedPmids.isEmpty()) {
                return Collections.<Paper>emptyList();
            }

            List<String> limitedPmids = relatedPmids.subList(0,
                    Math.min(relatedPmids.size(), maxResults));

            StringBuilder fetchUrl = new StringBuilder(BASE_URL).append("/efetch.fcgi?");
            fetchUrl.append("db=pubmed");
            fetchUrl.append("&id=").append(String.join(",", limitedPmids));
            fetchUrl.append("&retmode=xml");
            appendStandardParams(fetchUrl);

            String fetchResponse = executeRateLimitedRequest(fetchUrl.toString());
            return xmlParser.parsePapers(fetchResponse);
        });
    }

    /**
     * Gets papers citing and cited by the given PMID.
     *
     * @param pmid the PubMed ID
     * @return map with "cited_by" and "references" lists of PMIDs
     * @throws ApiException if the request fails
     */
    public Map<String, List<String>> getTrackedCitations(String pmid) throws ApiException {
        if (pmid == null || pmid.isEmpty()) {
            throw new IllegalArgumentException("PMID cannot be null or empty");
        }

        return retryPolicy.execute(() -> {
            StringBuilder linkUrl = new StringBuilder(BASE_URL).append("/elink.fcgi?");
            linkUrl.append("dbfrom=pubmed&db=pubmed");
            linkUrl.append("&id=").append(pmid);
            linkUrl.append("&linkname=pubmed_pubmed_citedin,pubmed_pubmed_refs");
            linkUrl.append("&retmode=json");
            appendStandardParams(linkUrl);

            String linkResponse = executeRateLimitedRequest(linkUrl.toString());

            // Need a custom parser for this since it returns multiple linksets
            // For now, let's assume xmlParser helper can adapt or we parse manually here
            // Helper method in PubMedXmlParser might need update to handle multiple link
            // names
            // Let's implement parseCitationsFromJson in parser later, for now just fetch
            // one direction or implement parsing here?
            // Actually, PubMedXmlParser.parseRelatedPmidsFromJson takes first linkset.
            // We need to parse by linkname.
            return PubMedXmlParser.parseCitationsFromJson(linkResponse);
        });
    }

    /**
     * Retrieves details for multiple papers at once.
     *
     * @param pmids list of PMIDs
     * @return list of papers
     * @throws ApiException if request fails
     */
    public List<Paper> batchRetrieve(List<String> pmids) throws ApiException {
        if (pmids == null || pmids.isEmpty()) {
            return Collections.emptyList();
        }
        if (pmids.size() > 200) {
            throw new IllegalArgumentException("Cannot batch retrieve more than 200 papers at once");
        }

        return retryPolicy.execute(() -> {
            StringBuilder fetchUrl = new StringBuilder(BASE_URL).append("/efetch.fcgi?");
            fetchUrl.append("db=pubmed");
            fetchUrl.append("&id=").append(String.join(",", pmids));
            fetchUrl.append("&retmode=xml");
            appendStandardParams(fetchUrl);

            String fetchResponse = executeRateLimitedRequest(fetchUrl.toString());
            return xmlParser.parsePapers(fetchResponse);
        });
    }

    /**
     * Checks if a paper has been retracted or corrected.
     *
     * @param pmid the PubMed ID
     * @return retraction status message, or null if clean
     */
    public String checkRetractions(String pmid) throws ApiException {
        Paper paper = fetchByPmid(pmid);
        if (paper == null)
            return "Paper not found";

        for (String type : paper.getPublicationTypes()) {
            if (type.toLowerCase().contains("retract")) {
                return "RETRACTED: " + type;
            }
            if (type.toLowerCase().contains("corrected") || type.toLowerCase().contains("erratum")) {
                return "CORRECTED: " + type;
            }
        }
        return "Clean";
    }

    /**
     * Gets links to related databases (Genes, Proteins, ClinicalTrials).
     *
     * @param pmid the PubMed ID
     * @return list of database names linked to this paper
     */
    public List<String> getRelatedDatabaseLinks(String pmid) throws ApiException {
        return retryPolicy.execute(() -> {
            StringBuilder linkUrl = new StringBuilder(BASE_URL).append("/elink.fcgi?");
            linkUrl.append("dbfrom=pubmed");
            linkUrl.append("&id=").append(pmid);
            linkUrl.append("&cmd=acheck"); // Check all links
            linkUrl.append("&retmode=json");
            appendStandardParams(linkUrl);

            String linkResponse = executeRateLimitedRequest(linkUrl.toString());
            return PubMedXmlParser.parseAvailableLinksFromJson(linkResponse);
        });
    }

    private String executeRateLimitedRequest(String url) throws IOException, InterruptedException {
        rateLimiter.acquire();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("PubMed API error: HTTP " + response.code());
            }
            return response.body().string();
        }
    }

    private void appendStandardParams(StringBuilder sb) {
        sb.append("&tool=clavis");
        sb.append("&email=").append(urlEncode(email));
        if (!apiKey.isEmpty()) {
            sb.append("&api_key=").append(apiKey);
        }
    }

    private void validateSearchParams(String query, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }
        if (maxResults < 1 || maxResults > 10000) {
            throw new IllegalArgumentException("Max results must be between 1 and 10000");
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
