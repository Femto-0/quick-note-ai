package com.quicknoteai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OllamaService {

    private final WebClient webClient = WebClient.create("http://localhost:11434");

    public Mono<String> getSummary(String noteContent) {
        Map<String, Object> requestBody = Map.of(
                "model", "llama3.2",
                "prompt", "Summarize this:\n\n" + noteContent,
                "stream", false
        );

        return webClient.post()
                .uri("/api/generate")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> response.get("response").toString().trim());
    }

    public Mono<String> determineMood(String noteContent){
        Map<String, Object> requestBody = Map.of(
                "model", "llama3.2",
                "prompt", "Determine the mood of the writer in one sentence:\n\n" + noteContent,
                "stream", false
        );

        return webClient.post()
                .uri("/api/generate")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> response.get("response").toString().trim());
    }


}
