package com.example.JourneyMate.external.cruises;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa la información completa de un crucero.
 * Incluye detalles del barco, itinerario, precios y cabinas disponibles.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CruiseDTO {

    /**
     * Identificador único del crucero.
     */
    private String id;

    /**
     * Nombre comercial del crucero.
     */
    private String nombreCrucero;

    /**
     * Nombre del barco.
     */
    private String nombreBarco;

    /**
     * URL del logo del barco.
     */
    private String logoBarco;

    /**
     * Fecha de salida del crucero.
     */
    private String fechaSalida;

    /**
     * Fecha de llegada del crucero.
     */
    private String fechaLlegada;

    /**
     * Duración del crucero en noches.
     */
    private Integer noches;

    /**
     * Puerto de salida del crucero.
     */
    private String puertoSalida;

    /**
     * Destino principal del crucero.
     */
    private String destino;

    /**
     * Precio base del crucero desde.
     */
    private Double precioDesde;

    /**
     * Tasas e impuestos asociados al crucero.
     */
    private Double tasasYImpuestos;

    /**
     * Código de la moneda utilizada (EUR, USD, etc.).
     */
    private String moneda;

    /**
     * Enlace externo para realizar la reserva.
     */
    private String linkReserva;

    /**
     * Imagen principal del crucero.
     */
    private String imagenPrincipal;

    /**
     * Galería de imágenes del crucero.
     */
    private List<String> galeriaImagenes;

    /**
     * Lista de paradas o itinerario diario del crucero.
     */
    private List<ItinerarioDTO> paradas;

    /**
     * Lista de cabinas disponibles en el crucero.
     */
    private List<CabinaDTO> cabinas;

    /**
     * DTO que representa una parada dentro del itinerario del crucero.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItinerarioDTO {

        /**
         * Día del itinerario.
         */
        private Integer dia;

        /**
         * Puerto de la parada.
         */
        private String puerto;

        /**
         * Región o área geográfica de la parada.
         */
        private String region;

        /**
         * Hora de llegada al puerto.
         */
        private String llegada;

        /**
         * Hora de salida del puerto.
         */
        private String salida;

        /**
         * Tipo de actividad: PORT (parada) o CRUISING (navegación).
         */
        private String tipo;
    }

    /**
     * DTO que representa una cabina disponible en el crucero.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CabinaDTO {

        /**
         * Tipo de cabina (Interior, Balcón, Suite, etc.).
         */
        private String tipo;

        /**
         * Descripción detallada de la cabina.
         */
        private String descripcion;

        /**
         * Precio de la cabina.
         */
        private Double precio;

        /**
         * Lista de amenidades incluidas en la cabina.
         */
        private List<String> amenidades;
    }
}