package com.example.JourneyMate.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public abstract class BaseExternalService {

    protected final RestTemplate restTemplate;

    protected BaseExternalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Este metodo centraliza la lógica duplicada de red y cabeceras
    protected JsonNode executeGetRequest(String url, String apiKey, String apiHost) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, JsonNode.class);
            return response.getBody();
        } catch (Exception e) {
            // Centralizamos el manejo de errores básico
            System.err.println("Error en la llamada API externa: " + e.getMessage());
            return null;
        }
    }
}
