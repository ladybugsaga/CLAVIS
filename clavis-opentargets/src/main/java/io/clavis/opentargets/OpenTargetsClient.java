package io.clavis.opentargets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.clavis.core.exception.ApiException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OpenTargetsClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenTargetsClient.class);
    private static final String API_URL = "https://api.platform.opentargets.org/api/v4/graphql";
    
    private final OkHttpClient httpClient;
    private final Gson gson;

    public OpenTargetsClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public JsonObject executeQuery(String query, JsonObject variables) throws ApiException {
        JsonObject payload = new JsonObject();
        payload.addProperty("query", query);
        if (variables != null) {
            payload.add("variables", variables);
        }

        RequestBody body = RequestBody.create(
                gson.toJson(payload),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("Open Targets API error: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            if (jsonResponse.has("errors")) {
                JsonArray errors = jsonResponse.getAsJsonArray("errors");
                String firstError = errors.get(0).getAsJsonObject().get("message").getAsString();
                throw new ApiException("GraphQL Error: " + firstError);
            }

            return jsonResponse.getAsJsonObject("data");
        } catch (IOException e) {
            throw new ApiException("Failed to communicate with Open Targets API", e);
        }
    }

    public JsonObject search(String queryString) throws ApiException {
        String query = """
            query SearchQuery($queryString: String!) {
              search(queryString: $queryString) {
                total
                hits {
                  id
                  name
                  entity
                  description
                }
              }
            }
            """;
        JsonObject variables = new JsonObject();
        variables.addProperty("queryString", queryString);
        return executeQuery(query, variables);
    }

    public JsonObject getTarget(String ensemblId) throws ApiException {
        String query = """
            query TargetQuery($ensemblId: String!) {
              target(ensemblId: $ensemblId) {
                id
                approvedSymbol
                approvedName
                description
                targetClass {
                    label
                }
                tractability {
                    id
                    modality
                    value
                }
              }
            }
            """;
        JsonObject variables = new JsonObject();
        variables.addProperty("ensemblId", ensemblId);
        return executeQuery(query, variables);
    }

    public JsonObject getDisease(String efoId) throws ApiException {
        String query = """
            query DiseaseQuery($efoId: String!) {
              disease(efoId: $efoId) {
                id
                name
                description
                therapeuticAreas {
                  id
                  name
                }
              }
            }
            """;
        JsonObject variables = new JsonObject();
        variables.addProperty("efoId", efoId);
        return executeQuery(query, variables);
    }

    public JsonObject getDrug(String chemblId) throws ApiException {
        String query = """
            query DrugQuery($chemblId: String!) {
              drug(chemblId: $chemblId) {
                id
                name
                description
                mechanismsOfAction {
                  rows {
                    mechanismOfAction
                    targetName
                  }
                }
                linkedDiseases {
                  count
                  rows {
                    id
                    name
                  }
                }
              }
            }
            """;
        JsonObject variables = new JsonObject();
        variables.addProperty("chemblId", chemblId);
        return executeQuery(query, variables);
    }

    public JsonObject getAssociations(String ensemblId) throws ApiException {
        String query = """
            query AssociationQuery($ensemblId: String!) {
              target(ensemblId: $ensemblId) {
                approvedSymbol
                associatedDiseases {
                  count
                  rows {
                    disease {
                      id
                      name
                    }
                    score
                  }
                }
              }
            }
            """;
        JsonObject variables = new JsonObject();
        variables.addProperty("ensemblId", ensemblId);
        return executeQuery(query, variables);
    }
}
