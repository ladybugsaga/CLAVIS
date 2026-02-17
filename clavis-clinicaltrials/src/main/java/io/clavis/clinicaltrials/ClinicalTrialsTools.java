package io.clavis.clinicaltrials;

import io.clavis.core.mcp.MCPTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Defines the 4 MCP tools for ClinicalTrials.gov:
 * - ct_search_condition — Search trials by condition/disease
 * - ct_search_intervention — Search trials by intervention/treatment
 * - ct_get_study — Get full study details by NCT ID
 * - ct_search_studies — General keyword search with optional status filter
 */
public class ClinicalTrialsTools {

    private final ClinicalTrialsClient client;

    public ClinicalTrialsTools() {
        this.client = new ClinicalTrialsClient();
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();
        tools.add(searchByCondition());
        tools.add(searchByIntervention());
        tools.add(getStudy());
        tools.add(searchStudies());
        return tools;
    }

    private MCPTool searchByCondition() {
        return new ClinicalTrialsTool(
                "ct_search_condition",
                "Search clinical trials by condition or disease (e.g. 'lung cancer', 'diabetes', 'alzheimers'). Returns trial summaries with NCT ID, status, phase, and sponsor.",
                Map.of(
                        "condition", "string",
                        "pageSize", "integer"),
                List.of("condition"),
                args -> {
                    try {
                        String condition = args.get("condition").getAsString();
                        int pageSize = args.has("pageSize") ? args.get("pageSize").getAsInt() : 10;
                        String json = client.searchByCondition(condition, pageSize);
                        return ClinicalTrialsJsonParser.parseStudySearch(json).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool searchByIntervention() {
        return new ClinicalTrialsTool(
                "ct_search_intervention",
                "Search clinical trials by intervention or treatment (e.g. 'pembrolizumab', 'CRISPR', 'radiation therapy'). Returns trial summaries.",
                Map.of(
                        "intervention", "string",
                        "pageSize", "integer"),
                List.of("intervention"),
                args -> {
                    try {
                        String intervention = args.get("intervention").getAsString();
                        int pageSize = args.has("pageSize") ? args.get("pageSize").getAsInt() : 10;
                        String json = client.searchByIntervention(intervention, pageSize);
                        return ClinicalTrialsJsonParser.parseStudySearch(json).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool getStudy() {
        return new ClinicalTrialsTool(
                "ct_get_study",
                "Get detailed information about a specific clinical trial by NCT ID (e.g. 'NCT04267848'). Returns full study details including eligibility, locations, and summary.",
                Map.of(
                        "nctId", "string"),
                List.of("nctId"),
                args -> {
                    try {
                        String nctId = args.get("nctId").getAsString();
                        String json = client.getStudy(nctId);
                        return ClinicalTrialsJsonParser.parseStudyDetail(json).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool searchStudies() {
        return new ClinicalTrialsTool(
                "ct_search_studies",
                "General search of clinical trials by keyword. Optionally filter by status (RECRUITING, COMPLETED, ACTIVE_NOT_RECRUITING, NOT_YET_RECRUITING, etc.).",
                Map.of(
                        "query", "string",
                        "status", "string",
                        "pageSize", "integer"),
                List.of("query"),
                args -> {
                    try {
                        String query = args.get("query").getAsString();
                        String status = args.has("status") ? args.get("status").getAsString() : null;
                        int pageSize = args.has("pageSize") ? args.get("pageSize").getAsInt() : 10;
                        String json = client.searchStudies(query, status, pageSize);
                        return ClinicalTrialsJsonParser.parseStudySearch(json).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }
}
