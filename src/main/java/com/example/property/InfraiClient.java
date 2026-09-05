package com.example.property;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class InfraiClient {
    private final InfraiConfig config;
    private final HttpClient http = HttpClient.newHttpClient();

    public InfraiClient(InfraiConfig config) { this.config = config; }

    public String post(String path, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.baseUrl() + path))
                .timeout(Duration.ofSeconds(20)).header("Authorization", "Bearer " + config.apiKey())
                .header("Content-Type", "application/json").method("POST", HttpRequest.BodyPublishers.ofString(json)).build();
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            if (body.contains("\"ok\":false")) throw new IllegalStateException("Infrai rejected request: " + body);
            if (response.statusCode() == 429) { Thread.sleep((long) Math.pow(2, attempt) * 500); continue; }
            if (response.statusCode() >= 500) throw new IllegalStateException("Infrai service response: " + response.statusCode());
            return body;
        }
        throw new IllegalStateException("Retry budget exhausted");
    }
}
