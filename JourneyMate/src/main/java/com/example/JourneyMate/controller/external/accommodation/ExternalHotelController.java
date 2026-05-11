package com.example.JourneyMate.controller.external.accommodation;

import com.example.JourneyMate.external.accommodations.HotelDTO;
import com.example.JourneyMate.external.accommodations.HotelDetailsDTO;
import com.example.JourneyMate.service.external.accommodation.IHotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador encargado de gestionar las operaciones relacionadas
 * con hoteles y alojamientos externos.
 * <p>
 * Permite buscar hoteles, obtener identificadores de destinos
 * y consultar detalles específicos de un alojamiento.
 */
@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalHotelController {
    /**
     * Servicio encargado de la integración con proveedores externos
     * de hoteles y alojamientos.
     */
    private final IHotelService hotelService;
    /**
     * Busca hoteles disponibles según los parámetros indicados.
     *
     * @param destId identificador del destino
     * @param searchType tipo de búsqueda utilizado por la API externa
     * @param checkinDate fecha de entrada del hospedaje
     * @param checkoutDate fecha de salida del hospedaje
     * @param adults cantidad de adultos
     * @param childrenAge edades de los niños, si aplica
     * @param roomQty cantidad de habitaciones
     * @param pageNo número de página para resultados paginados
     * @param currencyCode código de moneda para los precios
     * @return lista de hoteles encontrados o respuesta vacía si no hay resultados
     */
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
    /**
     * Obtiene el identificador de destino utilizado por la API externa
     * a partir del nombre de una ciudad o ubicación.
     *
     * @param name nombre del destino
     * @return identificador del destino encontrado
     */
    @GetMapping("/destination")
    public ResponseEntity<String> getDestinationId(@RequestParam String name) {
        String destId = hotelService.getDestinationId(name);

        if (destId == null || destId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(destId);
    }
    /**
     * Obtiene información detallada de un hotel específico.
     *
     * @param hotelId identificador del hotel
     * @param arrivalDate fecha de llegada
     * @param departureDate fecha de salida
     * @param adults cantidad de adultos
     * @param childrenAge edades de los niños, si aplica
     * @param roomQty cantidad de habitaciones
     * @param currencyCode código de moneda utilizado
     * @return detalles completos del hotel
     */
    @GetMapping("/details")
    public ResponseEntity<HotelDetailsDTO> getHotelDetails(
            @RequestParam String hotelId,
            @RequestParam String arrivalDate,
            @RequestParam String departureDate,
            @RequestParam(defaultValue = "1") Integer adults,
            @RequestParam(required = false) String childrenAge,
            @RequestParam(defaultValue = "1") Integer roomQty,
            @RequestParam(defaultValue = "EUR") String currencyCode) {

        HotelDetailsDTO details = hotelService.getHotelDetails(hotelId, arrivalDate, departureDate, adults, childrenAge, roomQty, currencyCode);
        return ResponseEntity.ok(details);
    }
}