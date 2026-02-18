package io.clavis.openfda;

import com.google.gson.JsonObject;
import io.clavis.core.mcp.MCPTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tool definitions for OpenFDA.
 */
public class OpenFdaTools {
    private static final Logger logger = LoggerFactory.getLogger(OpenFdaTools.class);
    private final OpenFdaClient client;

    public OpenFdaTools(OpenFdaClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();
        tools.add(createSearchAdverseEventsTool());
        tools.add(createSearchDrugLabelsTool());
        tools.add(createSearchEnforcementsTool());
        return tools;
    }

    public MCPTool createSearchAdverseEventsTool() {
        return new OpenFdaTool(
                "openfda_search_adverse_events",
                "Search drug adverse event reports (FAERS). Use 'patient.drug.medicinalproduct:DRUGNAME' or generic query.",
                Map.of(
                        "query", "The search query (e.g., 'patient.drug.medicinalproduct:aspirin')",
                        "limit", "Number of results (default 1, max 10)"
                ),
                List.of("query"),
                args -> {
                    String query = (String) args.get("query");
                    int limit = args.containsKey("limit") ? ((Double) args.get("limit")).intValue() : 1;
                    try {
                        return client.searchEvents(query, limit).toString();
                    } catch (Exception e) {
                        logger.error("Error in openfda_search_adverse_events", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }

    public MCPTool createSearchDrugLabelsTool() {
        return new OpenFdaTool(
                "openfda_search_drug_labels",
                "Search drug labeling (SPL) including warnings, usage, and dosage. Use 'openfda.brand_name:NAME' or similar.",
                Map.of(
                        "query", "The search query",
                        "limit", "Number of results (default 1, max 10)"
                ),
                List.of("query"),
                args -> {
                    String query = (String) args.get("query");
                    int limit = args.containsKey("limit") ? ((Double) args.get("limit")).intValue() : 1;
                    try {
                        return client.searchLabels(query, limit).toString();
                    } catch (Exception e) {
                        logger.error("Error in openfda_search_drug_labels", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }

    public MCPTool createSearchEnforcementsTool() {
        return new OpenFdaTool(
                "openfda_search_recalls",
                "Search drug recall enforcement reports. Use 'city:CITY' or 'reason_for_recall:REASON'.",
                Map.of(
                        "query", "The search query",
                        "limit", "Number of results (default 1, max 10)"
                ),
                List.of("query"),
                args -> {
                    String query = (String) args.get("query");
                    int limit = args.containsKey("limit") ? ((Double) args.get("limit")).intValue() : 1;
                    try {
                        return client.searchEnforcements(query, limit).toString();
                    } catch (Exception e) {
                        logger.error("Error in openfda_search_recalls", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }
}
