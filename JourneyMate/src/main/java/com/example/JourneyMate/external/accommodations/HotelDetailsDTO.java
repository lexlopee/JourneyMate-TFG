package com.example.JourneyMate.external.accommodations;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class HotelDetailsDTO {
    private DataPayload data;

    @Data
    public static class DataPayload {
        private String hotel_name;
        private String address;
        private String city;
        private String currency_code;
        private String accommodation_type_name;
        private int review_nr;
        private int available_rooms;

        // Fotos mapeadas desde el objeto 'rooms'
        private Map<String, RoomDetails> rooms;

        // Estructura de precios (Map<String, Object> o puedes crear clase si prefieres)
        private Map<String, Object> product_price_breakdown;

        // NUEVOS CAMPOS PARA EL FRONTEND DETALLADO:

        // Instalaciones (Most popular facilities)
        private FacilitiesBlock facilities_block;

        // Sostenibilidad
        private Sustainability sustainability;

        // Puntuación del desayuno
        private BreakfastScore breakfast_review_score;
    }

    @Data
    public static class RoomDetails {
        private List<Photo> photos;
    }

    @Data
    public static class Photo {
        private String url_max750;
    }

    // Clases internas para los nuevos detalles:

    @Data
    public static class FacilitiesBlock {
        private List<Facility> facilities;
        @Data
        public static class Facility {
            private String name;
            private String icon;
        }
    }

    @Data
    public static class Sustainability {
        private HotelPage hotel_page;
        @Data
        public static class HotelPage {
            private int has_badge;
            private String title;
            private String description;
        }
    }

    @Data
    public static class BreakfastScore {
        private double rating;
        private String review_score_word;
    }
}