package io.clavis.chembl;

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

public class ChEMBLClient {

    private static final Logger logger = LoggerFactory.getLogger(ChEMBLClient.class);
    private static final String API_BASE = "https://www.ebi.ac.uk/chembl/api/data";
    
    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;

    public ChEMBLClient() {
        this.httpClient = HttpClientFactory.createDefault();
        // ChEMBL is generous but let's be polite (e.g. 5 req/s)
        this.rateLimiter = new RateLimiter(5);
    }

    public String searchCompounds(String query, int limit) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = API_BASE + "/molecule/search?q=" + encodedQuery + "&format=json&limit=" + limit;
        return executeRequest(url);
    }

    public String getCompound(String chemblId) throws IOException {
        String url = API_BASE + "/molecule/" + chemblId + "?format=json";
        return executeRequest(url);
    }

    public String getDrugMechanism(String chemblId) throws IOException {
        String url = API_BASE + "/mechanism?molecule_chembl_id=" + chemblId + "&format=json";
        return executeRequest(url);
    }
    
    public String getBioactivity(String targetChemblId, String moleculeChemblId, int limit) throws IOException {
        StringBuilder url = new StringBuilder(API_BASE).append("/activity?format=json");
        if (targetChemblId != null) {
            url.append("&target_chembl_id=").append(targetChemblId);
        }
        if (moleculeChemblId != null) {
            url.append("&molecule_chembl_id=").append(moleculeChemblId);
        }
        url.append("&limit=").append(limit);
        return executeRequest(url.toString());
    }

    private String executeRequest(String url) throws IOException {
        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Accept-Encoding", "identity") // Avoid GZIP issues like in UniProt
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("ChEMBL API error " + response.code() + ": " + response.message());
            }
            return response.body().string();
        }
    }
}
