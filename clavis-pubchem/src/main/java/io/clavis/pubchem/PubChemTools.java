package io.clavis.pubchem;

import io.clavis.core.mcp.MCPTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Defines the 5 MCP tools for PubChem:
 * - pubchem_search_compound — Search by compound name
 * - pubchem_get_compound — Get properties by CID
 * - pubchem_get_description — Get compound description/summary
 * - pubchem_search_smiles — Search by SMILES structure
 * - pubchem_get_synonyms — Get compound synonyms
 */
public class PubChemTools {

    private final PubChemClient client;

    public PubChemTools() {
        this.client = new PubChemClient();
    }

    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();
        tools.add(searchCompound());
        tools.add(getCompound());
        tools.add(getDescription());
        tools.add(searchBySmiles());
        tools.add(getSynonyms());
        return tools;
    }

    private MCPTool searchCompound() {
        return new PubChemTool(
                "pubchem_search_compound",
                "Search PubChem compounds by name (e.g. 'aspirin', 'ibuprofen', 'caffeine'). Returns molecular properties including formula, weight, SMILES, and InChIKey.",
                Map.of("name", "string"),
                List.of("name"),
                args -> {
                    try {
                        String name = args.get("name").getAsString();
                        String json = client.searchByName(name);
                        return PubChemJsonParser.parsePropertySearch(json).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool getCompound() {
        return new PubChemTool(
                "pubchem_get_compound",
                "Get detailed compound properties by PubChem CID (compound ID). Returns molecular formula, weight, SMILES, InChIKey, XLogP, TPSA, complexity, and more.",
                Map.of("cid", "integer"),
                List.of("cid"),
                args -> {
                    try {
                        int cid = args.get("cid").getAsInt();
                        String json = client.getCompoundByCid(cid);
                        return PubChemJsonParser.parsePropertySearch(json).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool getDescription() {
        return new PubChemTool(
                "pubchem_get_description",
                "Get a textual description/summary of a compound by PubChem CID. Returns descriptions from multiple sources.",
                Map.of("cid", "integer"),
                List.of("cid"),
                args -> {
                    try {
                        int cid = args.get("cid").getAsInt();
                        String json = client.getCompoundDescription(cid);
                        return PubChemJsonParser.parseDescription(json).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool searchBySmiles() {
        return new PubChemTool(
                "pubchem_search_smiles",
                "Search PubChem by SMILES chemical structure notation. Returns matching compounds with properties.",
                Map.of("smiles", "string"),
                List.of("smiles"),
                args -> {
                    try {
                        String smiles = args.get("smiles").getAsString();
                        String json = client.searchBySmiles(smiles);
                        return PubChemJsonParser.parsePropertySearch(json).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }

    private MCPTool getSynonyms() {
        return new PubChemTool(
                "pubchem_get_synonyms",
                "Get all known synonyms (trade names, IUPAC names, common names) for a compound by PubChem CID.",
                Map.of("cid", "integer"),
                List.of("cid"),
                args -> {
                    try {
                        int cid = args.get("cid").getAsInt();
                        String json = client.getSynonyms(cid);
                        return PubChemJsonParser.parseSynonyms(json).toString();
                    } catch (IOException e) {
                        return "{\"error\": \"" + e.getMessage() + "\"}";
                    }
                });
    }
}
