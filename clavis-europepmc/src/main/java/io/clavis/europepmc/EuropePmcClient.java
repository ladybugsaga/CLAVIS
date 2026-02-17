package io.clavis.europepmc;

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
 * Client for Europe PMC REST API.
 */
public class EuropePmcClient {
    private static final String BASE_URL = "https://www.ebi.ac.uk/europepmc/webservices/rest";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final RetryPolicy retryPolicy;
    private final EuropePmcJsonParser parser;

    public EuropePmcClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(10); // 10 requests per second
        this.retryPolicy = RetryPolicy.defaultPolicy();
        this.parser = new EuropePmcJsonParser();
    }

    /**
     * Search Europe PMC for papers.
     *
     * @param query    search query
     * @param pageSize number of results
     * @return list of papers
     * @throws IOException on error
     */
    public List<Paper> search(String query, int pageSize) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/search"))
                .newBuilder()
                .addQueryParameter("query", query)
                .addQueryParameter("pageSize", String.valueOf(pageSize))
                .addQueryParameter("format", "json")
                .addQueryParameter("resultType", "core")
                .build();

        String response = executeRequest(url);
        return parser.parseSearchResults(response);
    }

    /**
     * Get details for a specific paper.
     *
     * @param id     paper ID
     * @param source source (e.g. "MED")
     * @return paper details or null
     * @throws IOException on error
     */
    public Paper getDetails(String id, String source) throws IOException {
        String query = "ext_id:" + id + " src:" + source;
        List<Paper> results = search(query, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Get citing papers.
     *
     * @param id       paper ID
     * @param source   source
     * @param pageSize number of results
     * @return list of citing papers
     * @throws IOException on error
     */
    public List<Paper> getCitations(String id, String source, int pageSize) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/" + source + "/" + id + "/citations"))
                .newBuilder()
                .addQueryParameter("pageSize", String.valueOf(pageSize))
                .addQueryParameter("format", "json")
                .build();

        String response = executeRequest(url);
        return parser.parseSearchResults(response);
    }

    /**
     * Get references.
     *
     * @param id       paper ID
     * @param source   source
     * @param pageSize number of results
     * @return list of references
     * @throws IOException on error
     */
    public List<Paper> getReferences(String id, String source, int pageSize) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/" + source + "/" + id + "/references"))
                .newBuilder()
                .addQueryParameter("pageSize", String.valueOf(pageSize))
                .addQueryParameter("format", "json")
                .build();

        String response = executeRequest(url);
        return parser.parseSearchResults(response);
    }

    private String executeRequest(HttpUrl url) throws IOException {
        try {
            return retryPolicy.execute(() -> {
                rateLimiter.acquire();
                Request request = new Request.Builder().url(url).build();
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("API error: " + response.code() + " " + response.message());
                    }
                    return Objects.requireNonNull(response.body()).string();
                }
            });
        } catch (io.clavis.core.exception.ApiException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
