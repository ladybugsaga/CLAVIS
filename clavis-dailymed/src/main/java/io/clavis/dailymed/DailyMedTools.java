package io.clavis.dailymed;

import io.clavis.core.mcp.MCPTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DailyMedTools {
    private static final Logger logger = LoggerFactory.getLogger(DailyMedTools.class);
    private final DailyMedClient client;

    public DailyMedTools(DailyMedClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();
        tools.add(createSearchSplsTool());
        tools.add(createGetSplDetailsTool());
        tools.add(createSearchDrugNamesTool());
        tools.add(createGetDrugClassesTool());
        tools.add(createGetNdcsBySetIdTool());
        return tools;
    }

    public MCPTool createSearchSplsTool() {
        return new DailyMedTool(
                "dailymed_search_spls",
                "Search drug labels (SPLs) by drug name. Returns SetIDs and titles.",
                Map.of(
                        "query", "The drug name to search for (e.g., 'aspirin')",
                        "page", "Page number (default 1)",
                        "pageSize", "Page size (default 20, max 100)"
                ),
                List.of("query"),
                args -> {
                    String query = (String) args.get("query");
                    int page = args.containsKey("page") ? ((Number) args.get("page")).intValue() : 1;
                    int pageSize = args.containsKey("pageSize") ? ((Number) args.get("pageSize")).intValue() : 20;
                    try {
                        return client.searchSpls(query, page, pageSize).toString();
                    } catch (Exception e) {
                        logger.error("Error in dailymed_search_spls", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }

    public MCPTool createGetSplDetailsTool() {
        return new DailyMedTool(
                "dailymed_get_spl_details",
                "Retrieve full metadata for a specific SPL by its SetID.",
                Map.of("setId", "The SetID of the SPL"),
                List.of("setId"),
                args -> {
                    String setId = (String) args.get("setId");
                    try {
                        return client.getSplDetails(setId).toString();
                    } catch (Exception e) {
                        logger.error("Error in dailymed_get_spl_details", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }

    public MCPTool createSearchDrugNamesTool() {
        return new DailyMedTool(
                "dailymed_search_drug_names",
                "Search for drug names matching a specific string.",
                Map.of("query", "The drug name fragment to search for"),
                List.of("query"),
                args -> {
                    String query = (String) args.get("query");
                    try {
                        return client.searchDrugNames(query).toString();
                    } catch (Exception e) {
                        logger.error("Error in dailymed_search_drug_names", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }

    public MCPTool createGetDrugClassesTool() {
        return new DailyMedTool(
                "dailymed_get_drug_classes",
                "Get drug classes associated with a specific drug name.",
                Map.of("drugName", "The drug name"),
                List.of("drugName"),
                args -> {
                    String drugName = (String) args.get("drugName");
                    try {
                        return client.getDrugClasses(drugName).toString();
                    } catch (Exception e) {
                        logger.error("Error in dailymed_get_drug_classes", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }

    public MCPTool createGetNdcsBySetIdTool() {
        return new DailyMedTool(
                "dailymed_get_ndcs_by_setid",
                "Retrieve National Drug Codes (NDCs) associated with a specific SetID.",
                Map.of("setId", "The SetID of the SPL"),
                List.of("setId"),
                args -> {
                    String setId = (String) args.get("setId");
                    try {
                        return client.getNdcsBySetId(setId).toString();
                    } catch (Exception e) {
                        logger.error("Error in dailymed_get_ndcs_by_setid", e);
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        );
    }
}
