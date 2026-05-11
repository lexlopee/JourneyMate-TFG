package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.cruises.CruiseDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Servicio encargado de consumir la API externa de cruceros.
 *
 * Proporciona acceso a destinos, puertos y búsqueda de cruceros disponibles
 * con diferentes filtros como fechas, puerto de salida y destino.
 */
public interface ICruiseService {

    /**
     * Obtiene la lista de destinos disponibles para cruceros.
     *
     * @return nodo JSON con los destinos disponibles
     */
    JsonNode getDestinations();

    /**
     * Obtiene la lista de puertos de salida disponibles.
     *
     * @return nodo JSON con los puertos disponibles
     */
    JsonNode getPorts();

    /**
     * Busca cruceros según los filtros proporcionados.
     *
     * @param startDate fecha de inicio del crucero
     * @param endDate fecha de fin del crucero
     * @param destination destino del crucero
     * @param departurePort puerto de salida
     * @param currency moneda en la que se muestran los precios
     * @param country país de referencia para la búsqueda
     * @return lista de cruceros en formato {@link CruiseDTO}
     */
    List<CruiseDTO> searchCruises(
            String startDate,
            String endDate,
            String destination,
            String departurePort,
            String currency,
            String country
    );
}