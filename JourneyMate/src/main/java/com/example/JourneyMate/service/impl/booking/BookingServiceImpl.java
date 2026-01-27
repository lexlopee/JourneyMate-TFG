package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.exception.custom.ExternalServiceException;
import com.example.JourneyMate.external.flights.BookingResponseDTO;
import com.example.JourneyMate.external.flights.FlightStopDTO;
import com.example.JourneyMate.service.booking.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    private final RestTemplate restTemplate;

    @Value("${booking.api.key}")
    private String apiKey;

    @Value("${booking.api.host}")
    private String apiHost;

    @Override
    public List<FlightStopDTO> getFlightSummary(String fromId, String toId, String departDate) {
        String url = String.format("https://%s/v1/flights/searchFlights?fromId=%s&toId=%s&departDate=%s",
                apiHost, fromId, toId, departDate);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Llamamos al DTO principal (el Wrapper)
            ResponseEntity<BookingResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, BookingResponseDTO.class
            );

            // Navegamos por el objeto: Response -> Data -> Aggregation -> Stops
            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData().getAggregation().getStops();
            }

            return List.of(); // Devolvemos lista vacía si no hay datos

        } catch (Exception e) {
            throw new ExternalServiceException("Booking API", "Error al procesar JSON: " + e.getMessage());
        }
    }
}