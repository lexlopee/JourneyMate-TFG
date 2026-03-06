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

        // Fotos generales de las habitaciones
        private Map<String, RoomDetails> rooms;

        // Precio total desglosado
        private Map<String, Object> product_price_breakdown;
    }

    @Data
    public static class RoomDetails {
        private List<Photo> photos;
    }

    @Data
    public static class Photo {
        private String url_max750;
    }
}