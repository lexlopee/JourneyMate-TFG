package com.example.JourneyMate.controller.external.accommodation;

import com.example.JourneyMate.external.accommodations.HotelDTO;
import com.example.JourneyMate.service.external.accommodation.IHotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalHotelController {

    private final IHotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<List<HotelDTO>> searchHotels(
            @RequestParam String destId,
            @RequestParam(defaultValue = "CITY") String searchType, // Obligatorio en la API
            @RequestParam String checkinDate, // Este mapeará a arrival_date
            @RequestParam String checkoutDate, // Este mapeará a departure_date
            @RequestParam(defaultValue = "2") Integer adults,
            @RequestParam(required = false) String childrenAge,
            @RequestParam(defaultValue = "1") Integer roomQty,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "EUR") String currencyCode) {

        // Pasamos el searchType al servicio
        List<HotelDTO> results = hotelService.searchHotels(
                destId, searchType, checkinDate, checkoutDate, adults, childrenAge, roomQty, pageNo, currencyCode
        );

        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }

    @GetMapping("/destination")
    public ResponseEntity<String> getDestinationId(@RequestParam String name) {
        String destId = hotelService.getDestinationId(name);

        if (destId == null || destId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(destId);
    }
}