package io.clavis.arxiv;

import io.clavis.core.http.HttpClientFactory;
import io.clavis.core.http.RateLimiter;
import io.clavis.core.http.RetryPolicy;
import io.clavis.core.models.Paper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Client for the arXiv API (Atom XML).
 * Rate limited to 1 request per second per arXiv policy.
 */
public class ArxivClient {
    private static final String BASE_URL = "http://export.arxiv.org/api/query";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;
    private final ArxivXmlParser parser;

    public ArxivClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(1); // arXiv asks for max 1 req/s
        this.retryPolicy = RetryPolicy.defaultPolicy();
        this.parser = new ArxivXmlParser();
    }

    /**
     * Search arXiv with a free-text query.
     *
     * @param query      search query (supports ti:, au:, cat:, all: prefixes)
     * @param maxResults max papers to return
     * @return list of papers
     */
    public List<Paper> search(String query, int maxResults) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL))
                .newBuilder()
                .addQueryParameter("search_query", query)
                .addQueryParameter("start", "0")
                .addQueryParameter("max_results", String.valueOf(maxResults))
                .addQueryParameter("sortBy", "submittedDate")
                .addQueryParameter("sortOrder", "descending")
                .build();

        String xml = executeRequest(url);
        return parser.parseSearchResults(xml);
    }

    /**
     * Get a paper by its arXiv ID.
     *
     * @param arxivId arXiv identifier (e.g. "2301.12345")
     * @return the paper or null if not found
     */
    public Paper getById(String arxivId) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL))
                .newBuilder()
                .addQueryParameter("id_list", arxivId)
                .build();

        String xml = executeRequest(url);
        List<Paper> results = parser.parseSearchResults(xml);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Search by author name.
     */
    public List<Paper> searchByAuthor(String authorName, int maxResults) throws IOException {
        return search("au:\"" + authorName + "\"", maxResults);
    }

    /**
     * Search by category.
     */
    public List<Paper> searchByCategory(String category, String query, int maxResults) throws IOException {
        String searchQuery = "cat:" + category;
        if (query != null && !query.isEmpty()) {
            searchQuery += " AND all:" + query;
        }
        return search(searchQuery, maxResults);
    }

    private String executeRequest(HttpUrl url) throws IOException {
        try {
            return retryPolicy.execute(() -> {
                rateLimiter.acquire();
                Request request = new Request.Builder().url(url).build();
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("arXiv API error: " + response.code() + " " + response.message());
                    }
                    return Objects.requireNonNull(response.body()).string();
                }
            });
        } catch (io.clavis.core.exception.ApiException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
