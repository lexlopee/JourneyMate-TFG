package com.example.JourneyMate.controller.external.transport;

import com.example.JourneyMate.external.cars.CarDTO;
import com.example.JourneyMate.service.external.transport.ICarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalCarController {

    private final ICarService carService;

    /**
     * PASO 1: Buscar ubicaciones para el alquiler.
     */
    @GetMapping("/locations")
    public ResponseEntity<List<Map<String, Object>>> getLocations(@RequestParam String query) {
        return ResponseEntity.ok(carService.searchCarLocation(query));
    }

    /**
     * PASO 2: Buscar coches disponibles basados en coordenadas.
     */
    @GetMapping("/search")
    public ResponseEntity<List<CarDTO>> searchCars(
            @RequestParam Double pick_up_latitude,
            @RequestParam Double pick_up_longitude,
            @RequestParam Double drop_off_latitude,
            @RequestParam Double drop_off_longitude,
            @RequestParam String pick_up_date,
            @RequestParam String drop_off_date,
            @RequestParam String pick_up_time,
            @RequestParam String drop_off_time,
            @RequestParam(defaultValue = "30") Integer driver_age,
            @RequestParam(defaultValue = "EUR") String currency_code) {

        return ResponseEntity.ok(carService.searchCars(
                pick_up_latitude, pick_up_longitude, drop_off_latitude, drop_off_longitude,
                pick_up_date, pick_up_time, drop_off_date, drop_off_time, driver_age, currency_code
        ));
    }
}