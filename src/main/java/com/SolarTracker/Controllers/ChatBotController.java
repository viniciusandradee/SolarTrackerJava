package com.SolarTracker.Controllers;

import com.SolarTracker.Services.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);

    private final OpenAIService openAIService;

    public ChatBotController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, String> request) {
        String userPrompt = request.get("prompt");

        if (userPrompt == null || userPrompt.isBlank()) {
            logger.warn("O prompt do usuário está vazio ou inválido.");
            return ResponseEntity.badRequest().body(Map.of("error", "O prompt do usuário é obrigatório."));
        }

        try {
            String response = openAIService.getChatbotResponse(userPrompt).block();
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            logger.error("Erro ao processar a solicitação do ChatBot: ", e);
            return ResponseEntity.status(500).body(Map.of("error", "Erro interno do servidor", "details", e.getMessage()));
        }
    }
}
