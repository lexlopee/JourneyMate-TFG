package com.example.JourneyMate.service.external.transport;

import com.example.JourneyMate.external.cars.CarDTO;
import com.example.JourneyMate.external.cars.CarLocationDTO;

import java.util.List;

/**
 * Interfaz para la gestión de servicios externos de alquiler de coches.
 * Conecta con la API de Booking (v18) para obtener ubicaciones y ofertas.
 */
public interface ICarService {

    /**
     * Busca ubicaciones (aeropuertos, ciudades, distritos) para alquiler de coches.
     * * @param query Texto de búsqueda (mínimo 2 caracteres).
     *
     * @param languageCode Código de idioma (ej: "es-es", "en-us").
     * @param countryFlag  Código del país para el icono/bandera (ej: "es", "us").
     * @return Lista de DTOs con las ubicaciones encontradas.
     */
    List<CarLocationDTO> searchCarLocation(String query, String languageCode, String countryFlag);

    /**
     * Busca ofertas de coches disponibles entre dos ubicaciones y fechas.
     * * @param pickUpId ID en Base64 de la ubicación de recogida (obtenido del autocomplete).
     *
     * @param dropOffId ID en Base64 de la ubicación de devolución (opcional, si es distinto al de recogida).
     * @param pDate     Fecha de recogida (YYYY-MM-DD).
     * @param pTime     Hora de recogida (HH:MM).
     * @param dDate     Fecha de devolución (YYYY-MM-DD).
     * @param dTime     Hora de devolución (HH:MM).
     * @param age       Edad del conductor (mínimo 18, defecto suele ser 30).
     * @param currency  Código de moneda (EUR, USD, etc.).
     * @return Lista de DTOs con los coches y precios disponibles.
     */
    List<CarDTO> searchCars(
            String pickUpId,
            String dropOffId,
            String pDate,
            String pTime,
            String dDate,
            String dTime,
            Integer age,
            String currency
    );
}