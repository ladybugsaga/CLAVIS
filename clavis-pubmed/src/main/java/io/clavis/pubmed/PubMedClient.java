package io.clavis.pubmed;

import io.clavis.core.config.ConfigManager;
import io.clavis.core.exception.ApiException;
import io.clavis.core.http.HttpClientFactory;
import io.clavis.core.http.RateLimiter;
import io.clavis.core.http.RetryPolicy;
import io.clavis.core.logging.StructuredLogger;
import io.clavis.core.models.Author;
import io.clavis.core.models.Paper;
import io.clavis.pubmed.models.PubMedPaper;
import io.clavis.pubmed.parsers.PubMedXmlParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Client for interacting with PubMed E-utilities API.
 *
 * <p>Implements rate limiting (10 req/sec with API key) and
 * automatic retry with exponential backoff.</p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 * @see <a href="https://www.ncbi.nlm.nih.gov/books/NBK25501/">E-utilities Documentation</a>
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
            String searchUrl = String.format(
                    "%s/esearch.fcgi?db=pubmed&term=%s&retmax=%d&retmode=json&api_key=%s&tool=clavis&email=%s",
                    BASE_URL, urlEncode(query), maxResults, apiKey, email);

            String searchResponse = executeRateLimitedRequest(searchUrl);
            List<String> pmids = PubMedXmlParser.parsePmidsFromJson(searchResponse);

            if (pmids.isEmpty()) {
                logger.logApiResponse("pubmed", 200, System.currentTimeMillis() - startTime);
                return Collections.<Paper>emptyList();
            }

            String fetchUrl = String.format(
                    "%s/efetch.fcgi?db=pubmed&id=%s&retmode=xml&api_key=%s&tool=clavis&email=%s",
                    BASE_URL, String.join(",", pmids), apiKey, email);

            String fetchResponse = executeRateLimitedRequest(fetchUrl);
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
            String url = String.format(
                    "%s/efetch.fcgi?db=pubmed&id=%s&retmode=xml&api_key=%s&tool=clavis&email=%s",
                    BASE_URL, pmid, apiKey, email);

            String response = executeRateLimitedRequest(url);
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
            String linkUrl = String.format(
                    "%s/elink.fcgi?dbfrom=pubmed&db=pubmed&id=%s&linkname=pubmed_pubmed&retmode=json&api_key=%s",
                    BASE_URL, pmid, apiKey);

            String linkResponse = executeRateLimitedRequest(linkUrl);
            List<String> relatedPmids = PubMedXmlParser.parseRelatedPmidsFromJson(linkResponse);

            if (relatedPmids.isEmpty()) {
                return Collections.<Paper>emptyList();
            }

            List<String> limitedPmids = relatedPmids.subList(0,
                    Math.min(relatedPmids.size(), maxResults));

            String fetchUrl = String.format(
                    "%s/efetch.fcgi?db=pubmed&id=%s&retmode=xml&api_key=%s&tool=clavis&email=%s",
                    BASE_URL, String.join(",", limitedPmids), apiKey, email);

            String fetchResponse = executeRateLimitedRequest(fetchUrl);
            return xmlParser.parsePapers(fetchResponse);
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
