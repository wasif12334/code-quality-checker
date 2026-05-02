package com.codequalitychecker.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AIAnalysisService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final HttpClient client = HttpClient.newHttpClient();

    public String analyzeCode(String code) {

        if (apiKey == null || apiKey.isBlank()) {
            return "Gemini API key is missing.";
        }

        String prompt = "You are a senior Java developer. Analyze this code and give:\n"
                + "- Bugs\n- Code Smells\n- Improvements\n- Best Practices\n\nCode:\n"
                + code;

        String requestBody = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "%s"
                }
              ]
            }
          ]
        }
        """.formatted(prompt.replace("\"", "\\\""));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/"
                            + model + ":generateContent?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "Error: " + response.body();
            }

            // Simple extraction (no JSON lib needed)
            String body = response.body();

            int start = body.indexOf("text");
            if (start == -1) return "No response from Gemini";

            return body.substring(start + 7, Math.min(start + 1000, body.length()));

        } catch (IOException | InterruptedException e) {
            return "Error calling Gemini: " + e.getMessage();
        }
    }
}