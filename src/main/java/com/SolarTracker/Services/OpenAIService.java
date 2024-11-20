package com.SolarTracker.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final WebClient webClient;

    // Definindo a URL da API diretamente na constante
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    public OpenAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(OPENAI_API_URL).build();
    }

    public Mono<String> getChatbotResponse(String prompt) {
        logger.info("Enviando prompt para OpenAI: {}", prompt);

        return webClient.method(HttpMethod.POST)
                .uri("https://api.openai.com/v1/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", List.of(Map.of("role", "user", "content", prompt)),
                        "max_tokens", 150,
                        "temperature", 0.7
                ))
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    logger.error("Erro da OpenAI: {}", errorBody);
                                    return Mono.error(new RuntimeException("Erro da OpenAI: " + errorBody));
                                })
                )
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        // Usando Jackson para parsear a resposta JSON
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(response);
                        // Extraindo o conteúdo da resposta do assistente
                        JsonNode messageNode = rootNode.path("choices").get(0).path("message").path("content");
                        return messageNode.asText(); // Retorna o texto da resposta do assistente
                    } catch (Exception e) {
                        logger.error("Erro ao processar a resposta: {}", e.getMessage());
                        return "Erro ao processar a resposta";
                    }
                })
                .doOnSuccess(response -> logger.info("Resposta recebida da OpenAI: {}", response));
    }
}

