import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        String apiKey = System.getenv("API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("GEMINI_API_KEY environment variable not set");
        }
        
        var url = """
            https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%s""".formatted(apiKey);
        
        var requestBody = """
            {
                "contents": [{
                    "parts":[{"text": "Explain how AI works"}]
                }],
                "generationConfig": {
                    "maxOutputTokens": 100
                }
            }""";

        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Format and print the response
            String jsonResponse = response.body();
            JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
            JsonObject jsonObject = jsonReader.readObject();
            
            // Extract the text from the response
            String text = jsonObject
                .getJsonArray("candidates")
                .getJsonObject(0)
                .getJsonObject("content")
                .getJsonArray("parts")
                .getJsonObject(0)
                .getString("text");

            // Create a timestamp for the filename
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "gemini_response_" + timestamp + ".txt";
            
            try {
                java.nio.file.Files.writeString(
                    java.nio.file.Path.of(filename),
                    String.format("""
                        Formatted Response:
                        ------------------
                        %s
                        ------------------""", text)
                );
                System.out.println("Response saved to: " + filename);
            } catch (java.io.IOException ioe) {
                LOGGER.log(Level.SEVERE, "Error saving response to file", ioe);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error making API call", e);
        }
    }
}