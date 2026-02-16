package io.clavis.semanticscholar;

import io.clavis.core.exception.ApiException;
import io.clavis.core.http.HttpClientFactory;
import io.clavis.core.http.RateLimiter;
import io.clavis.core.http.RetryPolicy;
import io.clavis.core.logging.StructuredLogger;
import io.clavis.core.models.Paper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Client for interacting with the Semantic Scholar Academic Graph API.
 *
 * <p>
 * Implements rate limiting and automatic retry with exponential backoff.
 * Supports optional API key for higher rate limits (1 req/s without key,
 * 10 req/s with key).
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 * @see <a href="https://api.semanticscholar.org/">Semantic Scholar API</a>
 */
public class SemanticScholarClient {

    private static final String GRAPH_API_BASE = "https://api.semanticscholar.org/graph/v1";
    private static final String RECOMMENDATIONS_API_BASE = "https://api.semanticscholar.org/recommendations/v1";

    private static final String PAPER_FIELDS = "paperId,externalIds,title,abstract,authors,journal,year,citationCount,referenceCount,url,publicationTypes,openAccessPdf,fieldsOfStudy";
    private static final String AUTHOR_FIELDS = "authorId,name,affiliations,paperCount,citationCount,hIndex,url";
    private static final String CITATION_FIELDS = "paperId,externalIds,title,abstract,authors,journal,year,citationCount,url";

    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    private final StructuredLogger logger;
    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;
    private final String apiKey;

    /**
     * Creates a new Semantic Scholar API client.
     *
     * @param apiKey optional API key for higher rate limits (can be null/empty)
     */
    public SemanticScholarClient(String apiKey) {
        this.logger = new StructuredLogger(SemanticScholarClient.class);
        this.httpClient = HttpClientFactory.createDefault();

        if (apiKey == null || apiKey.trim().isEmpty()) {
            this.apiKey = "";
            this.rateLimiter = new RateLimiter(1);
            logger.warn("No API key provided. Requests will be rate-limited to 1/second.");
        } else {
            this.apiKey = apiKey.trim();
            this.rateLimiter = new RateLimiter(10);
            logger.info("API key configured. Rate limit: 10 req/s.");
        }

        this.retryPolicy = RetryPolicy.defaultPolicy();
    }

    /**
     * Searches for papers matching the query.
     *
     * @param query      search query
     * @param maxResults maximum results (1-100)
     * @param year       optional year filter (e.g. "2024" or "2020-2024")
     * @param openAccess optional filter for open access papers only
     * @param venue      optional venue/journal filter
     * @return list of matching papers
     * @throws ApiException if the request fails
     */
    public List<Paper> search(String query, int maxResults, String year, Boolean openAccess, String venue)
            throws ApiException {
        validateSearchParams(query, maxResults);
        return retryPolicy.execute(() -> {
            StringBuilder url = new StringBuilder(GRAPH_API_BASE).append("/paper/search?");
            url.append("query=").append(urlEncode(query));
            url.append("&limit=").append(Math.min(maxResults, 100));
            url.append("&fields=").append(PAPER_FIELDS);

            if (year != null && !year.isEmpty()) {
                url.append("&year=").append(urlEncode(year));
            }
            if (openAccess != null && openAccess) {
                url.append("&openAccessPdf");
            }
            if (venue != null && !venue.isEmpty()) {
                url.append("&venue=").append(urlEncode(venue));
            }

            logger.info("[s2] API request: endpoint=paper/search, query=" + query + ", limit=" + maxResults);

            String response = executeRequest(url.toString());
            return SemanticScholarJsonParser.parsePaperSearchResults(response);
        });
    }

    /**
     * Fetches a single paper by its identifier.
     *
     * @param paperId S2 Paper ID, DOI, PMID, ArXiv ID, etc.
     *                Prefix with DOI:, PMID:, ArXiv:, etc. for external IDs.
     * @return the paper, or null if not found
     * @throws ApiException if the request fails
     */
    public Paper getPaper(String paperId) throws ApiException {
        return retryPolicy.execute(() -> {
            String url = GRAPH_API_BASE + "/paper/" + urlEncode(paperId)
                    + "?fields=" + PAPER_FIELDS;

            logger.info("[s2] API request: endpoint=paper/" + paperId);

            String response = executeRequest(url);
            return SemanticScholarJsonParser.parseSinglePaper(response);
        });
    }

    /**
     * Gets papers that cite the given paper.
     *
     * @param paperId    the paper identifier
     * @param maxResults maximum results (1-1000)
     * @return list of citing papers
     * @throws ApiException if the request fails
     */
    public List<Paper> getCitations(String paperId, int maxResults) throws ApiException {
        return retryPolicy.execute(() -> {
            String url = GRAPH_API_BASE + "/paper/" + urlEncode(paperId)
                    + "/citations?fields=" + CITATION_FIELDS
                    + "&limit=" + Math.min(maxResults, 1000);

            logger.info("[s2] API request: endpoint=paper/" + paperId + "/citations");

            String response = executeRequest(url);
            return SemanticScholarJsonParser.parseCitationResults(response);
        });
    }

    /**
     * Gets papers referenced by the given paper.
     *
     * @param paperId    the paper identifier
     * @param maxResults maximum results (1-1000)
     * @return list of referenced papers
     * @throws ApiException if the request fails
     */
    public List<Paper> getReferences(String paperId, int maxResults) throws ApiException {
        return retryPolicy.execute(() -> {
            String url = GRAPH_API_BASE + "/paper/" + urlEncode(paperId)
                    + "/references?fields=" + CITATION_FIELDS
                    + "&limit=" + Math.min(maxResults, 1000);

            logger.info("[s2] API request: endpoint=paper/" + paperId + "/references");

            String response = executeRequest(url);
            return SemanticScholarJsonParser.parseReferenceResults(response);
        });
    }

    /**
     * Searches for authors by name.
     *
     * @param name       author name query
     * @param maxResults maximum results (1-1000)
     * @return JSON string containing author results
     * @throws ApiException if the request fails
     */
    public String searchAuthors(String name, int maxResults) throws ApiException {
        return retryPolicy.execute(() -> {
            String url = GRAPH_API_BASE + "/author/search?query=" + urlEncode(name)
                    + "&limit=" + Math.min(maxResults, 1000)
                    + "&fields=" + AUTHOR_FIELDS;

            logger.info("[s2] API request: endpoint=author/search, query=" + name);

            return executeRequest(url);
        });
    }

    /**
     * Gets details for a specific author.
     *
     * @param authorId the Semantic Scholar author ID
     * @return JSON string containing author details
     * @throws ApiException if the request fails
     */
    public String getAuthor(String authorId) throws ApiException {
        return retryPolicy.execute(() -> {
            String url = GRAPH_API_BASE + "/author/" + urlEncode(authorId)
                    + "?fields=" + AUTHOR_FIELDS;

            logger.info("[s2] API request: endpoint=author/" + authorId);

            return executeRequest(url);
        });
    }

    /**
     * Gets papers by a specific author.
     *
     * @param authorId   the Semantic Scholar author ID
     * @param maxResults maximum results
     * @return list of papers by the author
     * @throws ApiException if the request fails
     */
    public List<Paper> getAuthorPapers(String authorId, int maxResults) throws ApiException {
        return retryPolicy.execute(() -> {
            String url = GRAPH_API_BASE + "/author/" + urlEncode(authorId)
                    + "/papers?fields=" + PAPER_FIELDS
                    + "&limit=" + Math.min(maxResults, 1000);

            logger.info("[s2] API request: endpoint=author/" + authorId + "/papers");

            String response = executeRequest(url);
            return SemanticScholarJsonParser.parseAuthorPapersResults(response);
        });
    }

    /**
     * Gets AI-powered paper recommendations from seed papers.
     *
     * @param positivePaperIds papers to use as positive examples
     * @param maxResults       maximum recommendations (1-500)
     * @return list of recommended papers
     * @throws ApiException if the request fails
     */
    public List<Paper> getRecommendations(List<String> positivePaperIds, int maxResults) throws ApiException {
        if (positivePaperIds == null || positivePaperIds.isEmpty()) {
            throw new ApiException("At least one seed paper ID is required");
        }

        return retryPolicy.execute(() -> {
            String url = RECOMMENDATIONS_API_BASE + "/papers/?fields=" + PAPER_FIELDS
                    + "&limit=" + Math.min(maxResults, 500);

            // Build JSON body
            StringBuilder body = new StringBuilder("{\"positivePaperIds\":[");
            for (int i = 0; i < positivePaperIds.size(); i++) {
                if (i > 0)
                    body.append(",");
                body.append("\"").append(positivePaperIds.get(i).replace("\"", "\\\"")).append("\"");
            }
            body.append("]}");

            logger.info("[s2] API request: endpoint=recommendations, seeds=" + positivePaperIds.size());

            String response = executePostRequest(url, body.toString());
            return SemanticScholarJsonParser.parseRecommendationResults(response);
        });
    }

    // ---- Internal helpers ----

    private String executeRequest(String url) throws IOException, InterruptedException {
        rateLimiter.acquire();
        Request.Builder builder = new Request.Builder().url(url);
        if (!apiKey.isEmpty()) {
            builder.header("x-api-key", apiKey);
        }
        Request request = builder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No body";
                throw new IOException("S2 API error " + response.code() + ": " + errorBody);
            }
            return response.body() != null ? response.body().string() : "";
        }
    }

    private String executePostRequest(String url, String jsonBody) throws IOException, InterruptedException {
        rateLimiter.acquire();
        RequestBody requestBody = RequestBody.create(jsonBody, JSON_MEDIA);
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);
        if (!apiKey.isEmpty()) {
            builder.header("x-api-key", apiKey);
        }
        Request request = builder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No body";
                throw new IOException("S2 API error " + response.code() + ": " + errorBody);
            }
            return response.body() != null ? response.body().string() : "";
        }
    }

    private void validateSearchParams(String query, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }
        if (maxResults < 1 || maxResults > 100) {
            throw new IllegalArgumentException("maxResults must be between 1 and 100");
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
