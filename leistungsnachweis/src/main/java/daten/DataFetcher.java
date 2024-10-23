package daten;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

public class DataFetcher {

    private final HttpClient client;
    private final Gson gson;

    public DataFetcher() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // Methode zum Abrufen der Daten von der Webschnittstelle
    public Dataset fetchDataset(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // JSON in Dataset deserialisieren
        return gson.fromJson(response.body(), Dataset.class);
    }
}
