package io.clavis.pubchem;

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
 * REST client for the PubChem PUG REST API.
 * Base URL: https://pubchem.ncbi.nlm.nih.gov/rest/pug
 * Returns JSON. No API key required.
 * Rate limit: 5 requests/second (NCBI guideline).
 */
public class PubChemClient {

    private static final Logger logger = LoggerFactory.getLogger(PubChemClient.class);
    private static final String PROLOG_BASE = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound";

    private final OkHttpClient httpClient;
    private final RateLimiter rateLimiter;

    public PubChemClient() {
        this.httpClient = HttpClientFactory.createDefault();
        this.rateLimiter = new RateLimiter(5);
    }

    /**
     * Search compounds by name — returns CID(s) and basic properties.
     */
    public String searchByName(String name) throws IOException {
        String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String url = PROLOG_BASE + "/name/" + encoded
                + "/property/MolecularFormula,MolecularWeight,IUPACName,CanonicalSMILES,InChIKey,XLogP,HBondDonorCount,HBondAcceptorCount/JSON";
        return executeRequest(url);
    }

    /**
     * Get compound by CID — full property set.
     */
    public String getCompoundByCid(int cid) throws IOException {
        String url = PROLOG_BASE + "/cid/" + cid
                + "/property/MolecularFormula,MolecularWeight,IUPACName,CanonicalSMILES,InChIKey,XLogP,HBondDonorCount,HBondAcceptorCount,ExactMass,TPSA,Complexity,Charge,IsomericSMILES/JSON";
        return executeRequest(url);
    }

    /**
     * Get compound description/summary by CID.
     */
    public String getCompoundDescription(int cid) throws IOException {
        String url = PROLOG_BASE + "/cid/" + cid + "/description/JSON";
        return executeRequest(url);
    }

    /**
     * Search compounds by SMILES substructure.
     */
    public String searchBySmiles(String smiles) throws IOException {
        String encoded = URLEncoder.encode(smiles, StandardCharsets.UTF_8);
        String url = PROLOG_BASE + "/smiles/" + encoded
                + "/property/MolecularFormula,MolecularWeight,IUPACName,CanonicalSMILES,InChIKey/JSON";
        return executeRequest(url);
    }

    /**
     * Get compound synonyms by CID.
     */
    public String getSynonyms(int cid) throws IOException {
        String url = PROLOG_BASE + "/cid/" + cid + "/synonyms/JSON";
        return executeRequest(url);
    }

    private String executeRequest(String url) throws IOException {
        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
        logger.debug("PubChem request: {}", url);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Accept-Encoding", "identity")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("PubChem API error " + response.code() + ": " + response.message());
            }
            return response.body() != null ? response.body().string() : "{}";
        }
    }
}
