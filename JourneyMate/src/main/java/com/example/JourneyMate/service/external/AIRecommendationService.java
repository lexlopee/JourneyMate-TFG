package com.example.JourneyMate.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AIRecommendationService {

    @Value("${google.ai.key}")
    private String apiKey;

    public String getRecommendation(String preferencia, String presupuesto) {
        RestTemplate restTemplate = new RestTemplate();

        // Creamos el mensaje para la IA (El Prompt)
        String prompt = "Actúa como experto en viajes. El usuario busca un viaje " + preferencia +
                " con presupuesto " + presupuesto + ". Recomienda un destino y 3 actividades brevemente.";

        // Estructura necesaria para la API de Google
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=";
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    GEMINI_URL + apiKey, requestBody, JsonNode.class);

            // Extraemos el texto de la respuesta de la IA
            return response.getBody()
                    .path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
        } catch (Exception e) {
            return "Lo siento, la IA está pensando en su próximo viaje. Inténtalo más tarde.";
        }
    }
}
