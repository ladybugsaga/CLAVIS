package io.clavis.ensembl;

import io.clavis.core.mcp.MCPTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnsemblTools {
    private final EnsemblClient client;

    public EnsemblTools(EnsemblClient client) {
        this.client = client;
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();

        // 1. ensembl_lookup_symbol
        tools.add(new EnsemblTool(
                "ensembl_lookup_symbol",
                "Lookup Ensembl identifiers and details for a gene symbol (e.g., 'BRCA2').",
                Map.of(
                    "symbol", "Gene symbol",
                    "species", "Species name (default 'human')"
                ),
                List.of("symbol"),
                args -> {
                    try {
                        String species = args.containsKey("species") ? (String) args.get("species") : "human";
                        return client.lookupSymbol((String) args.get("symbol"), species);
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 2. ensembl_lookup_id
        tools.add(new EnsemblTool(
                "ensembl_lookup_id",
                "Retrieve details for a specific Ensembl identifier (e.g., 'ENSG00000139618').",
                Map.of("id", "Ensembl identifier"),
                List.of("id"),
                args -> {
                    try {
                        return client.lookupId((String) args.get("id"));
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 3. ensembl_get_sequence
        tools.add(new EnsemblTool(
                "ensembl_get_sequence",
                "Retrieve genomic, cDNA, CDS, or protein sequence for an identifier.",
                Map.of(
                    "id", "Ensembl identifier",
                    "type", "Sequence type: genomic, cdna, cds, protein (default 'genomic')"
                ),
                List.of("id"),
                args -> {
                    try {
                        String type = args.containsKey("type") ? (String) args.get("type") : "genomic";
                        return client.getSequence((String) args.get("id"), type);
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 4. ensembl_get_overlap
        tools.add(new EnsemblTool(
                "ensembl_get_overlap",
                "Retrieve genomic features (e.g., variations) overlapping a region or identifier.",
                Map.of(
                    "id", "Ensembl identifier (e.g., gene ID)",
                    "feature", "Feature type: variation, gene, transcript, exon, cds, utr (default 'variation')"
                ),
                List.of("id"),
                args -> {
                    try {
                        String feature = args.containsKey("feature") ? (String) args.get("feature") : "variation";
                        return client.getOverlap((String) args.get("id"), feature);
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        // 5. ensembl_get_vep
        tools.add(new EnsemblTool(
                "ensembl_get_vep",
                "Retrieve Variant Effect Predictor (VEP) consequences for a specific HGVS or variant.",
                Map.of(
                    "variant", "HGVS expression or variant identifier",
                    "species", "Species name (default 'human')"
                ),
                List.of("variant"),
                args -> {
                    try {
                        String species = args.containsKey("species") ? (String) args.get("species") : "human";
                        return client.getVEP((String) args.get("variant"), species);
                    } catch (Exception e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                }
        ));

        return tools;
    }
}
