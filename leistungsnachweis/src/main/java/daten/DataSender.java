package daten;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DataSender {

    private static final String POST_URL = "http://localhost:8080/v1/result";
    private static final Gson gson = new Gson();

    /**
     * Sendet die berechneten Ergebnisse per POST an den Server.
     * @param results Liste von Result-Objekten, die gesendet werden sollen
     * @throws Exception falls ein Fehler bei der Anfrage auftritt
     */
    public void sendResults(List<Result> results) throws Exception {
        // JSON-String aus der Liste der Result-Objekte erzeugen
        String jsonPayload = gson.toJson(new ResultPayload(results));

        // POST-Anfrage vorbereiten
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(POST_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        // HTTP-Client erstellen
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Statuscode und Antwort anzeigen
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }

    // Hilfsklasse für das JSON-Format des Payloads
    static class ResultPayload {
        private List<Result> result;

        public ResultPayload(List<Result> result) {
            this.result = result;
        }
    }
}
