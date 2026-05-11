package com.example.JourneyMate.external.accommodations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO principal que representa la respuesta de detalles de un hotel
 * proveniente de una API externa de alojamientos.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelDetailsDTO {

    /**
     * Indica si la petición fue exitosa.
     */
    private boolean status;

    /**
     * Mensaje asociado a la respuesta de la API.
     */
    private String message;

    /**
     * Contenedor principal con los datos del hotel.
     */
    private DataPayload data;

    /**
     * Representa el payload con toda la información detallada del hotel.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataPayload {

        /**
         * Nombre del hotel.
         */
        @JsonProperty("hotel_name")
        private String hotelName;

        /**
         * Dirección del hotel.
         */
        private String address;

        /**
         * Ciudad donde se encuentra el hotel.
         */
        private String city;

        /**
         * Código de la moneda utilizada en los precios.
         */
        @JsonProperty("currency_code")
        private String currencyCode;

        /**
         * Tipo de alojamiento (hotel, apartamento, etc.).
         */
        @JsonProperty("accommodation_type_name")
        private String accommodationTypeName;

        /**
         * Puntuación general del hotel basada en reseñas.
         */
        @JsonProperty("review_score")
        private Double reviewScore;

        /**
         * Descripción textual de la puntuación del hotel.
         */
        @JsonProperty("review_score_word")
        private String reviewScoreWord;

        /**
         * Número total de reseñas recibidas.
         */
        @JsonProperty("review_nr")
        private int reviewNr;

        /**
         * Número de habitaciones disponibles.
         */
        @JsonProperty("available_rooms")
        private int availableRooms;

        /**
         * Desglose de precios del alojamiento.
         */
        @JsonProperty("product_price_breakdown")
        private PriceBreakdown productPriceBreakdown;

        /**
         * Mapa de habitaciones disponibles con sus detalles.
         */
        private Map<String, RoomDetails> rooms;

        /**
         * Información sobre instalaciones del alojamiento.
         */
        @JsonProperty("facilities_block")
        private FacilitiesBlock facilitiesBlock;

        /**
         * Puntuación del desayuno del hotel.
         */
        @JsonProperty("breakfast_review_score")
        private BreakfastScore breakfastReviewScore;
    }

    /**
     * Representa el desglose de precios del hotel.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceBreakdown {

        /**
         * Precio total con impuestos incluidos.
         */
        @JsonProperty("all_inclusive_amount")
        private PriceValue allInclusiveAmount;

        /**
         * Precio bruto del alojamiento.
         */
        @JsonProperty("gross_amount")
        private PriceValue grossAmount;

        /**
         * Cantidad excluida del precio total.
         */
        @JsonProperty("excluded_amount")
        private PriceValue excludedAmount;

        /**
         * Cantidad descontada si aplica.
         */
        @JsonProperty("discounted_amount")
        private PriceValue discountedAmount;

        /**
         * Número de estancias consideradas.
         */
        @JsonProperty("nr_stays")
        private int nrStays;
    }

    /**
     * Representa un valor monetario con su moneda.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceValue {

        /**
         * Valor numérico del precio.
         */
        private double value;

        /**
         * Código de la moneda (EUR, USD, etc.).
         */
        private String currency;

        /**
         * Representación del importe redondeado en formato string.
         */
        @JsonProperty("amount_rounded")
        private String amountRounded;
    }

    /**
     * Información de una habitación del hotel.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoomDetails {

        /**
         * Lista de fotos asociadas a la habitación.
         */
        private List<Photo> photos;
    }

    /**
     * Representa una fotografía de una habitación.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photo {

        /**
         * URL de la imagen en resolución máxima.
         */
        @JsonProperty("url_max750")
        private String urlMax750;
    }

    /**
     * Bloque que contiene las instalaciones del hotel.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FacilitiesBlock {

        /**
         * Lista de instalaciones disponibles.
         */
        private List<Facility> facilities;
    }

    /**
     * Representa una instalación del hotel.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Facility {

        /**
         * Nombre de la instalación.
         */
        private String name;

        /**
         * Icono asociado a la instalación.
         */
        private String icon;
    }

    /**
     * Información de puntuación del desayuno.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BreakfastScore {

        /**
         * Valor numérico de la puntuación del desayuno.
         */
        private double rating;

        /**
         * Descripción textual de la puntuación del desayuno.
         */
        @JsonProperty("review_score_word")
        private String reviewScoreWord;
    }
}