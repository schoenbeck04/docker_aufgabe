package app;

import daten.DataFetcher;
import daten.DataSender;
import daten.Dataset;
import daten.Event;
import daten.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            // Die URL der API, von der die Daten abgerufen werden sollen
            String url = "http://localhost:8080/v1/dataset";

            // DataFetcher zum Abrufen der Daten initialisieren
            DataFetcher dataFetcher = new DataFetcher();

            // Daten von der API abrufen
            Dataset dataset = dataFetcher.fetchDataset(url);

            // Gesamtnutzungszeit pro Kunde berechnen
            Map<String, Long> customerUsageTime = calculateUsageTime(dataset.getEvents());

            // Prozesse und berechnete Nutzungszeiten anzeigen
            displayUsageTimes(customerUsageTime);

            // Ergebnisse in das gewünschte Format konvertieren
            List<Result> results = convertToResults(customerUsageTime);

            // Ergebnisse an das System senden
            DataSender dataSender = new DataSender();
            dataSender.sendResults(results);

        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen oder Senden der Daten: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Methode zur Berechnung der Gesamtnutzungszeit für jeden Kunden.
     * @param events Liste von Events (start und stop)
     * @return Map mit der CustomerID und der gesamten Nutzungszeit
     */
    public static Map<String, Long> calculateUsageTime(List<Event> events) {
        // Map zum Speichern der Startzeitpunkte für jeden Workload pro Kunde
        Map<String, Long> startTimeMap = new HashMap<>();
        // Map zur Speicherung der Gesamtnutzungszeit pro Kunde
        Map<String, Long> customerUsageTime = new HashMap<>();

        // Phase 1: Sammle alle Start-Ereignisse
        for (Event event : events) {
            if (event.getEventType().equals("start")) {
                String customerId = event.getCustomerId();
                String workloadId = event.getWorkloadId();
                String key = customerId + "-" + workloadId; // Kombination aus Kunde und Workload als Key

                // Start-Ereignis speichern (Startzeit merken)
                startTimeMap.put(key, event.getTimestamp());
            }
        }

        // Phase 2: Verarbeite alle Stop-Ereignisse und berechne die Nutzungszeit
        for (Event event : events) {
            if (event.getEventType().equals("stop")) {
                String customerId = event.getCustomerId();
                String workloadId = event.getWorkloadId();
                String key = customerId + "-" + workloadId; // Kombination aus Kunde und Workload als Key

                // Stop-Ereignis: Zeitdifferenz berechnen und zur Gesamtnutzungszeit addieren
                if (startTimeMap.containsKey(key)) {
                    long startTime = startTimeMap.get(key);
                    long endTime = event.getTimestamp();
                    long usageTime = endTime - startTime;

                    // Nutzungszeit zur Gesamtsumme pro Kunde hinzufügen
                    customerUsageTime.put(customerId, customerUsageTime.getOrDefault(customerId, 0L) + usageTime);

                    // Entferne den Start-Eintrag, da der Prozess abgeschlossen ist
                    startTimeMap.remove(key);
                }
            }
        }

        return customerUsageTime;
    }

    /**
     * Gibt die berechneten Gesamtnutzungszeiten der Kunden auf der Konsole aus.
     * @param customerUsageTime Map mit CustomerID und deren Nutzungszeit
     */
    public static void displayUsageTimes(Map<String, Long> customerUsageTime) {
        System.out.println("Berechnete Nutzungszeit pro Kunde:");
        for (Map.Entry<String, Long> entry : customerUsageTime.entrySet()) {
            System.out.println("Customer ID: " + entry.getKey() + " - Nutzungszeit: " + entry.getValue() + " ms");
        }
        System.out.println();
    }

    /**
     * Konvertiert die berechnete Nutzungszeit in eine Liste von Result-Objekten.
     * @param customerUsageTime Map mit CustomerId und deren Nutzungszeit
     * @return Liste von Result-Objekten für die POST-Anfrage
     */
    public static List<Result> convertToResults(Map<String, Long> customerUsageTime) {
        List<Result> results = new ArrayList<>();
        for (Map.Entry<String, Long> entry : customerUsageTime.entrySet()) {
            results.add(new Result(entry.getKey(), entry.getValue()));
        }
        return results;
    }
}
