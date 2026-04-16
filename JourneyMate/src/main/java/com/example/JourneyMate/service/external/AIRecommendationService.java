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
        String prompt = "Actúa como experto en viajes. El usuario busca un viaje " + preferencia +
                " con presupuesto " + presupuesto + ". Recomienda un destino y 3 actividades brevemente.";
        return callGemini(prompt);
    }

    public String getItinerary(String query) {
        String prompt = "Actúa como un agente de viajes profesional y entusiasta. " +
                "Crea un itinerario detallado paso a paso para la siguiente petición: '" + query + "'. " +
                "Instrucciones: " +
                "1. Divide el plan por días (Día 1, Día 2, etc.). " +
                "2. Para cada día incluye: Actividad de mañana, sugerencia para el almuerzo, actividad de tarde y lugar para cenar. " +
                "3. Menciona lugares reales y específicos. " +
                "4. Usa formato Markdown (### para días, negritas para lugares).";
        return callGemini(prompt);
    }

    private String callGemini(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    GEMINI_URL + apiKey, requestBody, JsonNode.class);

            return response.getBody()
                    .path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
        } catch (Exception e) {
            return "Error llamando a la IA: " + e.getMessage();
        }
    }
}
