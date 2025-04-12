
/*
no longer using OpenAI since I kept running into Error 429: Too Many Requests
 */

package com.quicknoteai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OpenAiService {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;

    public OpenAiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> getSummary(String noteContent) {
        // Create the prompt
        String prompt = "Summarize :\n\n" + noteContent;

        // Construct request body
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                        Map.of("role", "system", "content", "You are a helpful assistant that summarizes notes."),
                        Map.of("role", "user", "content", prompt)
                },
                "temperature", 0.7
        );

        // Make the API request using WebClient
        return webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (java.util.List<Map<String, Object>>) response.get("choices");
                    var firstChoice = choices.get(0);
                    var message = (Map<String, Object>) firstChoice.get("message");
                    return message.get("content").toString().trim();
                });
    }
}
