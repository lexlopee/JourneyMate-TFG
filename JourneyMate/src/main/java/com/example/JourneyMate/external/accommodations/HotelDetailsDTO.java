package com.example.JourneyMate.external.accommodations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class HotelDetailsDTO {
    private boolean status;
    private String message;
    private DataPayload data;

    @Data
    public static class DataPayload {
        private String hotel_name;
        private String address;
        private String city;
        private String currency_code;
        private String accommodation_type_name;
        private Double review_score;
        private String review_score_word;
        private int review_nr;

        // Estructura de precios robusta
        private PriceBreakdown product_price_breakdown;

        private Map<String, RoomDetails> rooms;
        private FacilitiesBlock facilities_block;
        private BreakfastScore breakfast_review_score;
    }

    // --- NUEVAS CLASES PARA EL PRECIO (Crucial para que no salga "menos precio") ---

    @Data
    public static class PriceBreakdown {
        private PriceValue all_inclusive_amount; // El precio final
        private PriceValue gross_amount;          // El precio base (neto)
        private PriceValue excluded_amount;       // Impuestos y cargos aparte
        private PriceValue discounted_amount;     // Lo que se ha ahorrado
        private int nr_stays;                     // Número de noches real
    }

    @Data
    public static class PriceValue {
        private double value;
        private String currency;
        private String amount_rounded;
    }

    // --- CLASES DE SOPORTE YA EXISTENTES ---

    @Data
    public static class RoomDetails {
        private List<Photo> photos;
    }

    @Data
    public static class Photo {
        private String url_max750;
    }

    @Data
    public static class FacilitiesBlock {
        private List<Facility> facilities;
    }

    @Data
    public static class Facility {
        private String name;
        private String icon;
    }

    @Data
    public static class BreakfastScore {
        private double rating;
        private String review_score_word;
    }
}