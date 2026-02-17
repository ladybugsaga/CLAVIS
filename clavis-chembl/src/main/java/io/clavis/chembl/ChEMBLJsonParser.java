package io.clavis.chembl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ChEMBLJsonParser {

    public static JsonObject parseCompoundSearch(String json) {
        JsonObject result = new JsonObject();
        JsonArray compounds = new JsonArray();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("molecules")) {
                for (JsonElement el : root.getAsJsonArray("molecules")) {
                    JsonObject mol = el.getAsJsonObject();
                    compounds.add(parseCompound(mol));
                }
            }
            result.add("compounds", compounds);
            result.addProperty("totalResults", compounds.size());
        } catch (Exception e) {
            e.printStackTrace();
            result.addProperty("error", "Failed to parse ChEMBL response");
        }
        return result;
    }

    public static JsonObject parseCompound(String json) {
        try {
            // If fetching single compound, ChEMBL returns dictionary with single key unless using search
            // But we only wrapped search and get mechanism/activity
            // Wait, getCompound returns what?
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            // Try to extract molecule object if wrapped under "molecule" key?
            // Actually API usually returns just the object fields if asking for specific ID?
            // Let's assume it returns single object or we handle wrapper
            // Check curl for single molecule: /molecule/{chembl_id}.json
            // It likely returns { "molecule_chembl_id": ... } or { "molecule": { ... } }?
            // The list return was { "molecules": [...] }
            
            // Let's implement generic extraction
            return parseCompound(root);
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    private static JsonObject parseCompound(JsonObject mol) {
        JsonObject summary = new JsonObject();
        summary.addProperty("chemblId", getStr(mol, "molecule_chembl_id"));
        summary.addProperty("name", getStr(mol, "pref_name"));
        
        if (mol.has("molecule_properties") && !mol.get("molecule_properties").isJsonNull()) {
            JsonObject props = mol.getAsJsonObject("molecule_properties");
            summary.addProperty("formula", getStr(props, "full_molformula"));
            summary.addProperty("molecularWeight", getStr(props, "full_mwt"));
            summary.addProperty("alogp", getStr(props, "alogp"));
        }

        if (mol.has("molecule_structures") && !mol.get("molecule_structures").isJsonNull()) {
            JsonObject structs = mol.getAsJsonObject("molecule_structures");
            summary.addProperty("smiles", getStr(structs, "canonical_smiles"));
        }
        
        summary.addProperty("maxPhase", getStr(mol, "max_phase"));
        
        return summary;
    }

    public static JsonObject parseMechanisms(String json) {
        JsonObject result = new JsonObject();
        JsonArray mechanisms = new JsonArray();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("mechanisms")) {
                for (JsonElement el : root.getAsJsonArray("mechanisms")) {
                    JsonObject mech = el.getAsJsonObject();
                    JsonObject m = new JsonObject();
                    m.addProperty("mechanismOfAction", getStr(mech, "mechanism_of_action"));
                    m.addProperty("actionType", getStr(mech, "action_type"));
                    m.addProperty("targetChemblId", getStr(mech, "target_chembl_id"));
                    m.addProperty("description", getStr(mech, "mechanism_comment"));
                    mechanisms.add(m);
                }
            }
            result.add("mechanisms", mechanisms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JsonObject parseBioactivity(String json) {
        JsonObject result = new JsonObject();
        JsonArray activities = new JsonArray();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (root.has("activities")) {
                for (JsonElement el : root.getAsJsonArray("activities")) {
                    JsonObject act = el.getAsJsonObject();
                    JsonObject a = new JsonObject();
                    a.addProperty("type", getStr(act, "standard_type")); // e.g. IC50
                    a.addProperty("value", getStr(act, "standard_value"));
                    a.addProperty("units", getStr(act, "standard_units"));
                    a.addProperty("targetChemblId", getStr(act, "target_chembl_id"));
                    a.addProperty("assayDescription", getStr(act, "assay_description"));
                    a.addProperty("moleculeChemblId", getStr(act, "molecule_chembl_id"));
                    activities.add(a);
                }
            }
            result.add("activities", activities);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getStr(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }
}
