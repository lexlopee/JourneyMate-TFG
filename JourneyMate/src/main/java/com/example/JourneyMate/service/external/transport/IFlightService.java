package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.flights.FlightDTO;
import com.example.JourneyMate.external.flights.FlightDetailsDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Servicio encargado de gestionar la integración con la API externa de vuelos.
 *
 * Permite buscar ubicaciones, consultar vuelos disponibles y obtener
 * detalles completos de un vuelo específico.
 */
public interface IFlightService {

    /**
     * Busca ubicaciones (ciudades o aeropuertos) según una consulta.
     *
     * @param query nombre de ciudad o aeropuerto
     * @return nodo JSON con resultados de ubicaciones
     */
    JsonNode searchLocation(String query);

    /**
     * Busca vuelos disponibles según los parámetros de búsqueda.
     *
     * @param fromId aeropuerto o ciudad de origen
     * @param toId aeropuerto o ciudad de destino
     * @param departDate fecha de salida
     * @param returnDate fecha de regreso (opcional)
     * @param stops número de escalas (0 = directo, 1 = una escala, etc.)
     * @param pageNo número de página de resultados
     * @param adults número de pasajeros adultos
     * @param childrenAge edad de los niños (opcional / no usado actualmente)
     * @param sort criterio de ordenación de resultados
     * @param cabinClass clase del vuelo (ECONOMY, BUSINESS, etc.)
     * @param currencyCode moneda de los precios (EUR, USD, etc.)
     * @return lista de vuelos en formato {@link FlightDTO}
     */
    List<FlightDTO> searchFlights(
            String fromId,
            String toId,
            String departDate,
            String returnDate,
            String stops,
            Integer pageNo,
            Integer adults,
            String childrenAge,
            String sort,
            String cabinClass,
            String currencyCode
    );

    /**
     * Obtiene los detalles completos de un vuelo específico.
     *
     * @param token identificador único del vuelo
     * @param currencyCode moneda de visualización
     * @return detalles del vuelo en formato {@link FlightDetailsDTO}
     */
    FlightDetailsDTO getFlightDetails(String token, String currencyCode);
}