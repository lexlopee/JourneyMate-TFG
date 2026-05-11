package com.example.JourneyMate.controller.external.transport;

import com.example.JourneyMate.external.cars.CarDTO;
import com.example.JourneyMate.external.cars.CarLocationDTO;
import com.example.JourneyMate.service.external.transport.ICarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador encargado de gestionar servicios externos
 * relacionados con alquiler de vehículos.
 * <p>
 * Permite buscar ubicaciones disponibles y consultar
 * vehículos según diferentes criterios de alquiler.
 */
@RestController
@RequestMapping("/api/v1/external/cars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalCarController {

    /**
     * Servicio encargado de la integración con APIs externas
     * de alquiler de vehículos.
     */
    private final ICarService carService;

    /**
     * Obtiene sugerencias de ubicaciones para alquiler de vehículos.
     * <p>
     * Utilizado principalmente para autocompletado de ciudades
     * o puntos de recogida.
     *
     * @param query texto ingresado por el usuario
     * @return lista de ubicaciones encontradas
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<CarLocationDTO>> autocomplete(
            @RequestParam String query) {

        List<CarLocationDTO> results = carService.searchCarLocation(
                query,
                "es",
                "es"
        );

        return results.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(results);
    }

    /**
     * Busca vehículos disponibles para alquiler según los
     * parámetros especificados.
     *
     * @param pickUpId identificador del punto de recogida
     * @param dropOffId identificador del punto de devolución
     * @param pickUpDate fecha de recogida del vehículo
     * @param pickUpTime hora de recogida del vehículo
     * @param dropOffDate fecha de devolución del vehículo
     * @param dropOffTime hora de devolución del vehículo
     * @param driverAge edad del conductor
     * @param currencyCode código de moneda utilizado
     * @param carType tipo de vehículo solicitado
     * @return lista de vehículos disponibles
     */
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
                pickUpId,
                dropOffId,
                pickUpDate,
                pickUpTime,
                dropOffDate,
                dropOffTime,
                driverAge,
                currencyCode,
                carType
        );

        return cars.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(cars);
    }
}