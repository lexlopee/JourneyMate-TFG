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
        List<CarLocationDTO> results = carService.searchCarLocation(query, "en-us", "us");
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CarDTO>> search(
            @RequestParam String pickUpId,
            @RequestParam(required = false) String dropOffId,
            @RequestParam String pDate,
            @RequestParam String pTime,
            @RequestParam String dDate,
            @RequestParam String dTime,
            @RequestParam(defaultValue = "30") Integer age,
            @RequestParam(defaultValue = "USD") String currency) {

        List<CarDTO> cars = carService.searchCars(pickUpId, dropOffId, pDate, pTime, dDate, dTime, age, currency);
        return cars.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(cars);
    }
}