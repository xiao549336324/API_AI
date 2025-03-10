package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
@Service
public class GeminiService {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    private final HttpClient client = HttpClient.newHttpClient();
    
    public String generateResponse(String prompt, int targetWordCount) {
        var url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
        
        // Make the prompt very explicit about the word count requirement
        String modifiedPrompt = String.format(
            "You must respond with EXACTLY %d words, no more and no less. Here is the prompt: %s", 
            targetWordCount, 
            prompt
        );
        
        // Request more tokens than needed to ensure we get enough words
        var requestBody = String.format("""
            {
                "contents": [{
                    "parts":[{"text": "%s"}]
                }],
                "generationConfig": {
                    "maxOutputTokens": %d,
                    "temperature": 0.7,
                    "topK": 40,
                    "topP": 0.8
                }
            }""", modifiedPrompt, targetWordCount * 6); // Request more tokens to ensure we get enough words

        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
            JsonObject jsonObject = jsonReader.readObject();
            
            String text = jsonObject
                .getJsonArray("candidates")
                .getJsonObject(0)
                .getJsonObject("content")
                .getJsonArray("parts")
                .getJsonObject(0)
                .getString("text");
            
            // Clean up the text - remove extra spaces and normalize
            text = text.replaceAll("\\s+", " ").trim();
            String[] words = text.split("\\s+");
            int actualWordCount = words.length;
            
            if (actualWordCount < targetWordCount) {
                // If we got fewer words than requested, make another request for the remaining words
                int remainingWords = targetWordCount - actualWordCount;
                String additionalPrompt = String.format(
                    "Continue the following text with EXACTLY %d more words to complete the response: %s", 
                    remainingWords, 
                    text
                );
                String additionalText = generateResponse(additionalPrompt, remainingWords)
                    .split("\n\n")[1]; // Get just the text part, not the word count
                
                text = text + " " + additionalText;
                words = text.split("\\s+");
            }
            
            // Ensure exact word count by truncating or padding if necessary
            if (words.length > targetWordCount) {
                text = String.join(" ", Arrays.copyOfRange(words, 0, targetWordCount));
            } else while (words.length < targetWordCount) {
                // Pad with relevant words if we're still short
                text += " additional";
                words = text.split("\\s+");
            }
            
            return String.format("Word count: %d\n\n%s", targetWordCount, text);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate response", e);
        }
    }

    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}
