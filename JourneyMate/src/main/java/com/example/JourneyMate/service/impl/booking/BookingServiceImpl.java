package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.config.app.BookingProperties;
import com.example.JourneyMate.exception.custom.ExternalServiceException;
import com.example.JourneyMate.external.flights.BookingResponseDTO;
import com.example.JourneyMate.external.flights.FlightStopDTO;
import com.example.JourneyMate.service.booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final RestTemplate restTemplate;
    private final BookingProperties bookingProperties;

    @Override
    public List<FlightStopDTO> getFlightSummary(String fromId, String toId, String departDate) {

        String url = String.format(
                "https://%s/v1/flights/searchFlights?fromId=%s&toId=%s&departDate=%s",
                bookingProperties.getHost(),
                fromId,
                toId,
                departDate
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", bookingProperties.getKey());
        headers.set("X-RapidAPI-Host", bookingProperties.getHost());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<BookingResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    BookingResponseDTO.class
            );

            if (response.getBody() != null
                    && response.getBody().getData() != null
                    && response.getBody().getData().getAggregation() != null) {

                return response.getBody().getData().getAggregation().getStops();
            }

            return List.of();

        } catch (Exception e) {
            throw new ExternalServiceException(
                    "Booking API",
                    "Error al consumir el servicio externo: " + e.getMessage()
            );
        }
    }
}
