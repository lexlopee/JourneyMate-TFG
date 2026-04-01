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
@CrossOrigin(origins = "*")
public class ExternalCarController {

    private final ICarService carService;

    @GetMapping("/autocomplete")
    public ResponseEntity<List<CarLocationDTO>> autocomplete(@RequestParam String query) {
        // Ajustado a idioma español por defecto como pediste
        List<CarLocationDTO> results = carService.searchCarLocation(query, "es", "es");
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CarDTO>> search(
            @RequestParam String pickUpId,
            @RequestParam(required = false) String dropOffId,
            @RequestParam String pickUpDate,
            @RequestParam String pickUpTime,
            @RequestParam String dropOffDate,
            @RequestParam String dropOffTime,
            @RequestParam(defaultValue = "30") Integer driverAge,
            @RequestParam(defaultValue = "EUR") String currencyCode,
            @RequestParam(required = false) String carType) {

        List<CarDTO> cars = carService.searchCars(
                pickUpId, dropOffId, pickUpDate, pickUpTime,
                dropOffDate, dropOffTime, driverAge, currencyCode, carType
        );

        return cars.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(cars);
    }
}