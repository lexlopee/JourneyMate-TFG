package com.example.JourneyMate.service.external.accommodation;

import com.example.JourneyMate.external.accommodations.HotelDTO;
import com.example.JourneyMate.external.accommodations.HotelDetailsDTO;

import java.util.List;

/**
 * Interfaz que define las operaciones para el consumo
 * de la API externa de hoteles (Booking API).
 *
 * Proporciona métodos para búsqueda de destinos, listado de hoteles
 * y obtención de detalles de un hotel específico.
 */
public interface IHotelService {

    /**
     * Obtiene el identificador de destino (dest_id) a partir del nombre de una ciudad.
     *
     * Este método es útil para convertir una búsqueda textual (ej: "Madrid")
     * en un identificador válido para la API externa.
     *
     * @param cityName nombre de la ciudad o destino
     * @return identificador del destino o null si no se encuentra
     */
    String getDestinationId(String cityName);

    /**
     * Busca hoteles en la API externa según los parámetros proporcionados.
     *
     * @param destId identificador del destino
     * @param searchType tipo de búsqueda (ej: CITY, REGION)
     * @param checkinDate fecha de entrada
     * @param checkoutDate fecha de salida
     * @param adults número de adultos
     * @param childrenAge edad de los niños (si aplica)
     * @param roomQty número de habitaciones
     * @param pageNo número de página de resultados
     * @param currencyCode código de moneda (ej: EUR, USD)
     * @return lista de hoteles en formato {@link HotelDTO}
     */
    List<HotelDTO> searchHotels(
            String destId,
            String searchType,
            String checkinDate,
            String checkoutDate,
            Integer adults,
            String childrenAge,
            Integer roomQty,
            Integer pageNo,
            String currencyCode
    );

    /**
     * Obtiene información detallada de un hotel específico.
     *
     * @param hotelId identificador del hotel
     * @param arrivalDate fecha de entrada
     * @param departureDate fecha de salida
     * @param adults número de adultos
     * @param childrenAge edad de los niños
     * @param roomQty número de habitaciones
     * @param currencyCode código de moneda
     * @return detalles del hotel en formato {@link HotelDetailsDTO}
     */
    HotelDetailsDTO getHotelDetails(
            String hotelId,
            String arrivalDate,
            String departureDate,
            Integer adults,
            String childrenAge,
            Integer roomQty,
            String currencyCode
    );
}