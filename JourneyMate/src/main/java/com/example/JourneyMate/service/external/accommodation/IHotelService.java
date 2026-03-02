package com.example.JourneyMate.service.external.accommodation;

import com.example.JourneyMate.external.accommodations.HotelDTO;
import com.example.JourneyMate.external.accommodations.HotelDetailsDTO;

import java.util.List;

public interface IHotelService {

    /**
     * Obtiene el dest_id de una ciudad a partir de su nombre (ej: "Madrid").
     * Útil para el autocompletado en la interfaz.
     */
    String getDestinationId(String cityName);

    /**
     * Busca hoteles utilizando la API externa de Booking.
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

    HotelDetailsDTO getHotelDetails(String hotelId, String arrivalDate, String departureDate, Integer adults, String childrenAge, Integer roomQty);

}
