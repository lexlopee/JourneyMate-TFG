package com.example.JourneyMate.controller.external.transport;

import com.example.JourneyMate.external.cars.CarDTO;
import com.example.JourneyMate.external.cars.CarLocationDTO;
import com.example.JourneyMate.service.external.transport.ICarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/external/cars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Importante para que tu React/Angular no bloquee la petición
public class ExternalCarController {

    private final ICarService carService;

    /**
     * Endpoint para el autocompletado de ubicaciones de recogida/devolución.
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<CarLocationDTO>> autocomplete(
            @RequestParam String query,
            @RequestParam(required = false) String languageCode,
            @RequestParam(required = false) String countryFlag) {

        List<CarLocationDTO> results = carService.searchCarLocation(query, languageCode, countryFlag);
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }

    /**
     * Endpoint para buscar los coches disponibles.
     */
    @GetMapping("/search")
    public ResponseEntity<List<CarDTO>> search(
            @RequestParam String pickUpId, // Usar el ID en Base64 devuelto por autocomplete
            @RequestParam(required = false) String dropOffId,
            @RequestParam String pDate,    // Formato: YYYY-MM-DD
            @RequestParam String pTime,    // Formato: HH:MM
            @RequestParam String dDate,    // Formato: YYYY-MM-DD
            @RequestParam String dTime,    // Formato: HH:MM
            @RequestParam(defaultValue = "30") Integer age,
            @RequestParam(defaultValue = "EUR") String currency) {

        List<CarDTO> cars = carService.searchCars(pickUpId, dropOffId, pDate, pTime, dDate, dTime, age, currency);
        return cars.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(cars);
    }
}