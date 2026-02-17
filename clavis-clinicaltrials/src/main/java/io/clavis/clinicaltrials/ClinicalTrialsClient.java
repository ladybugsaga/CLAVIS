package io.clavis.clinicaltrials;

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
 * REST client for the ClinicalTrials.gov API v2.
 * Base URL: https://clinicaltrials.gov/api/v2
 * Returns JSON natively. No API key required.
 */
public class ClinicalTrialsClient {

    private static final Logger logger = LoggerFactory.getLogger(ClinicalTrialsClient.class);
    private static final String API_BASE = "https://clinicaltrials.gov/api/v2";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;

    public ClinicalTrialsClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(3); // Polite rate limit
    }

    /**
     * Search studies by condition/disease keyword.
     */
    public String searchByCondition(String condition, int pageSize) throws IOException {
        String encoded = URLEncoder.encode(condition, StandardCharsets.UTF_8);
        String url = API_BASE + "/studies?query.cond=" + encoded
                + "&pageSize=" + pageSize
                + "&format=json&countTotal=true"
                + "&fields=NCTId,BriefTitle,OverallStatus,Condition,InterventionName,Phase,EnrollmentCount,StartDate,PrimaryCompletionDate,LeadSponsorName,BriefSummary";
        return executeRequest(url);
    }

    /**
     * Search studies by intervention/treatment keyword.
     */
    public String searchByIntervention(String intervention, int pageSize) throws IOException {
        String encoded = URLEncoder.encode(intervention, StandardCharsets.UTF_8);
        String url = API_BASE + "/studies?query.intr=" + encoded
                + "&pageSize=" + pageSize
                + "&format=json&countTotal=true"
                + "&fields=NCTId,BriefTitle,OverallStatus,Condition,InterventionName,Phase,EnrollmentCount,StartDate,LeadSponsorName,BriefSummary";
        return executeRequest(url);
    }

    /**
     * Get a single study by NCT ID.
     */
    public String getStudy(String nctId) throws IOException {
        String url = API_BASE + "/studies/" + nctId + "?format=json";
        return executeRequest(url);
    }

    /**
     * Search studies by general keyword with optional status filter.
     */
    public String searchStudies(String query, String status, int pageSize) throws IOException {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        StringBuilder url = new StringBuilder(API_BASE)
                .append("/studies?query.term=").append(encoded)
                .append("&pageSize=").append(pageSize)
                .append("&format=json&countTotal=true")
                .append("&fields=NCTId,BriefTitle,OverallStatus,Condition,InterventionName,Phase,EnrollmentCount,StartDate,LeadSponsorName,BriefSummary");
        if (status != null && !status.isBlank()) {
            url.append("&filter.overallStatus=").append(status);
        }
        return executeRequest(url.toString());
    }

    private String executeRequest(String url) throws IOException {
        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
        logger.debug("ClinicalTrials request: {}", url);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Accept-Encoding", "identity")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("ClinicalTrials API error " + response.code() + ": " + response.message());
            }
            return response.body() != null ? response.body().string() : "{}";
        }
    }
}
