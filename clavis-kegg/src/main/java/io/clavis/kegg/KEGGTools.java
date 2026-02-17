package io.clavis.kegg;

import io.clavis.core.mcp.MCPTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Defines the 5 MCP tools for KEGG:
 * - kegg_search_pathways
 * - kegg_get_pathway
 * - kegg_search_genes
 * - kegg_get_linked_pathways
 * - kegg_search_compounds
 */
public class KEGGTools {

    private final KEGGClient client;

    public KEGGTools() {
        this.client = new KEGGClient();
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();
        tools.add(searchPathways());
        tools.add(getPathway());
        tools.add(searchGenes());
        tools.add(getLinkedPathways());
        tools.add(searchCompounds());
        return tools;
    }

    private MCPTool searchPathways() {
        return new KEGGTool(
                "kegg_search_pathways",
                "Search KEGG pathways by keyword (e.g. 'cancer', 'glycolysis', 'apoptosis'). Returns pathway IDs and names.",
                Map.of("query", "string"),
                List.of("query"),
                args -> {
                    try {
                        String query = args.get("query").getAsString();
                        String text = client.findPathways(query);
                        return KEGGParser.parseTabDelimited(text).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool getPathway() {
        return new KEGGTool(
                "kegg_get_pathway",
                "Get detailed information about a KEGG entry by ID. Works for pathways (e.g. 'hsa00010'), compounds ('C00002'), diseases ('H00001'), drugs ('D00001'), and genes ('hsa:7157').",
                Map.of("keggId", "string"),
                List.of("keggId"),
                args -> {
                    try {
                        String keggId = args.get("keggId").getAsString();
                        String text = client.getEntry(keggId);
                        return KEGGParser.parseFlatFile(text).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool searchGenes() {
        return new KEGGTool(
                "kegg_search_genes",
                "Search KEGG genes by keyword (e.g. 'TP53', 'BRCA1', 'insulin receptor'). Returns gene IDs and descriptions.",
                Map.of("query", "string"),
                List.of("query"),
                args -> {
                    try {
                        String query = args.get("query").getAsString();
                        String text = client.findGenes(query);
                        return KEGGParser.parseTabDelimited(text).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool getLinkedPathways() {
        return new KEGGTool(
                "kegg_get_linked_pathways",
                "Find all pathways linked to a specific gene (e.g. 'hsa:7157' for TP53). Returns pathway IDs associated with the gene.",
                Map.of("geneId", "string"),
                List.of("geneId"),
                args -> {
                    try {
                        String geneId = args.get("geneId").getAsString();
                        String text = client.getLinkedPathways(geneId);
                        return KEGGParser.parseTabDelimited(text).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool searchCompounds() {
        return new KEGGTool(
                "kegg_search_compounds",
                "Search KEGG compounds by name or keyword (e.g. 'aspirin', 'glucose', 'ATP'). Returns compound IDs and names.",
                Map.of("query", "string"),
                List.of("query"),
                args -> {
                    try {
                        String query = args.get("query").getAsString();
                        String text = client.findCompounds(query);
                        return KEGGParser.parseTabDelimited(text).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }
}
