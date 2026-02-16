package io.clavis.uniprot;

public class DebugUniProt {
    public static void main(String[] args) {
        try {
            UniProtClient client = new UniProtClient();
            System.out.println("Searching for insulin...");
            String response = client.searchProteins("insulin", 1, "9606", true);
            System.out.println("Raw Response:");
            System.out.println(response);

            System.out.println("\nParsing...");
            com.google.gson.JsonObject result = UniProtJsonParser.parseSearchResults(response);
            System.out.println("Parsed Result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
