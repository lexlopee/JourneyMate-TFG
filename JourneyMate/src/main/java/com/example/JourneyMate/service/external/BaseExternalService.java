package com.example.JourneyMate.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Clase base para servicios de integración con APIs externas.
 *
 * Centraliza la lógica común de ejecución de peticiones HTTP,
 * gestión de headers y manejo básico de errores.
 *
 * Todas las clases de servicios externos (vuelos, hoteles, coches, etc.)
 * deben extender esta clase para reutilizar la lógica de comunicación.
 */
public abstract class BaseExternalService {

    protected final RestTemplate restTemplate;

    /**
     * Constructor base que inyecta el cliente HTTP {@link RestTemplate}.
     *
     * @param restTemplate cliente HTTP utilizado para llamadas externas
     */
    protected BaseExternalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Ejecuta una petición HTTP GET a una API externa con cabeceras estándar de RapidAPI.
     *
     * Este método centraliza:
     * - Configuración de headers (API Key y Host)
     * - Ejecución de la petición HTTP
     * - Manejo básico de errores
     *
     * @param url URL completa del endpoint a consumir
     * @param apiKey clave de autenticación de la API
     * @param apiHost host requerido por la API (RapidAPI)
     * @return respuesta en formato {@link JsonNode}, o null si ocurre un error
     */
    protected JsonNode executeGetRequest(String url, String apiKey, String apiHost) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", apiKey);
        headers.set("x-rapidapi-host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            return response.getBody();

        } catch (Exception e) {
            System.err.println("Error en la llamada API externa: " + e.getMessage());
            return null;
        }
    }
}