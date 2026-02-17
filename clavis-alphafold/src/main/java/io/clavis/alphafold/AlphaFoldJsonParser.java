package io.clavis.alphafold;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.clavis.core.util.JsonUtils;

/**
 * Parses AlphaFold Protein Structure Database API responses.
 */
public class AlphaFoldJsonParser {

    /**
     * Format a prediction entry from the AlphaFold API.
     */
    public JsonObject formatPrediction(JsonObject prediction) {
        JsonObject result = new JsonObject();

        result.addProperty("entryId", JsonUtils.getString(prediction, "entryId", ""));
        result.addProperty("gene", JsonUtils.getString(prediction, "gene", ""));
        result.addProperty("uniprotAccession", JsonUtils.getString(prediction, "uniprotAccession", ""));
        result.addProperty("uniprotId", JsonUtils.getString(prediction, "uniprotId", ""));
        result.addProperty("uniprotDescription", JsonUtils.getString(prediction, "uniprotDescription", ""));
        result.addProperty("taxId", JsonUtils.getInt(prediction, "taxId", 0));
        result.addProperty("organismScientificName",
                JsonUtils.getString(prediction, "organismScientificName", ""));
        result.addProperty("uniprotStart", JsonUtils.getInt(prediction, "uniprotStart", 0));
        result.addProperty("uniprotEnd", JsonUtils.getInt(prediction, "uniprotEnd", 0));
        result.addProperty("modelCreatedDate",
                JsonUtils.getString(prediction, "modelCreatedDate", ""));

        // Confidence metrics
        result.addProperty("globalMetricType",
                JsonUtils.getString(prediction, "globalMetricType", ""));
        if (prediction.has("globalMetricValue")) {
            result.addProperty("globalMetricValue",
                    prediction.get("globalMetricValue").getAsDouble());
        }

        // File URLs
        result.addProperty("pdbUrl", JsonUtils.getString(prediction, "pdbUrl", ""));
        result.addProperty("cifUrl", JsonUtils.getString(prediction, "cifUrl", ""));
        result.addProperty("paeImageUrl", JsonUtils.getString(prediction, "paeImageUrl", ""));

        // Web link
        String uniprotAcc = JsonUtils.getString(prediction, "uniprotAccession", "");
        if (!uniprotAcc.isEmpty()) {
            result.addProperty("url", "https://alphafold.ebi.ac.uk/entry/" + uniprotAcc);
        }

        return result;
    }

    /**
     * Format a prediction list (the API returns arrays).
     */
    public JsonObject formatPredictionList(JsonArray predictions) {
        JsonObject result = new JsonObject();
        result.addProperty("count", predictions.size());
        JsonArray entries = new JsonArray();
        for (JsonElement e : predictions) {
            entries.add(formatPrediction(e.getAsJsonObject()));
        }
        result.add("predictions", entries);
        return result;
    }

    /**
     * Format a confidence summary.
     */
    public JsonObject formatConfidenceSummary(JsonObject prediction) {
        JsonObject result = new JsonObject();
        result.addProperty("entryId", JsonUtils.getString(prediction, "entryId", ""));
        result.addProperty("gene", JsonUtils.getString(prediction, "gene", ""));
        result.addProperty("uniprotAccession", JsonUtils.getString(prediction, "uniprotAccession", ""));

        // Confidence
        result.addProperty("globalMetricType",
                JsonUtils.getString(prediction, "globalMetricType", "pLDDT"));
        if (prediction.has("globalMetricValue")) {
            result.addProperty("globalMetricValue",
                    prediction.get("globalMetricValue").getAsDouble());
        }

        // Confidence interpretation
        if (prediction.has("globalMetricValue")) {
            double plddt = prediction.get("globalMetricValue").getAsDouble();
            String confidence;
            if (plddt >= 90) confidence = "Very high (pLDDT >= 90)";
            else if (plddt >= 70) confidence = "Confident (70 <= pLDDT < 90)";
            else if (plddt >= 50) confidence = "Low (50 <= pLDDT < 70)";
            else confidence = "Very low (pLDDT < 50)";
            result.addProperty("confidenceInterpretation", confidence);
        }

        result.addProperty("paeImageUrl", JsonUtils.getString(prediction, "paeImageUrl", ""));
        return result;
    }
}
