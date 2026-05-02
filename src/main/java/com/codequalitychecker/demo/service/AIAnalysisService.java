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

        // ✅ Validate API key
        if (apiKey == null || apiKey.isBlank()) {
            return "❌ Gemini API key is missing in application.properties";
        }

        // ✅ Prompt for AI
        String prompt = """
                You are a senior Java code reviewer.

                Analyze the following code and provide:
                - Bugs
                - Code Smells
                - Improvements
                - Best Practices

                Code:
                """ + code;

        // ✅ JSON request body
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

            // ✅ Correct Gemini API URL
            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + model
                    + ":generateContent?key="
                    + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            // ✅ Debug output (VERY IMPORTANT)
            System.out.println("===== GEMINI STATUS CODE =====");
            System.out.println(response.statusCode());

            System.out.println("===== GEMINI RAW RESPONSE =====");
            System.out.println(response.body());

            // ❌ API error handling
            if (response.statusCode() != 200) {
                return "❌ Gemini API Error: " + response.body();
            }

            // ✅ Return raw response (you can improve parsing later)
            return response.body();

        } catch (IOException | InterruptedException e) {
            return "❌ Error calling Gemini API: " + e.getMessage();
        }
    }
}