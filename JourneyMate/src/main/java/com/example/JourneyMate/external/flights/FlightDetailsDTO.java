package com.example.JourneyMate.external.flights;

import lombok.Data;
import java.util.List;

@Data
public class FlightDetailsDTO {
    private DataPayload data;

    @Data
    public static class DataPayload {
        private String token;
        private List<Segment> segments;
        private PriceBreakdown priceBreakdown;
    }

    @Data
    public static class Segment {
        private Airport departureAirport;
        private Airport arrivalAirport;
        private String departureTime;
        private String arrivalTime;
        private List<Leg> legs;
    }

    @Data
    public static class Airport {
        private String code;
        private String cityName;
    }

    @Data
    public static class Leg {
        private List<CarrierData> carriersData;
    }

    @Data
    public static class CarrierData {
        private String name;
        private String logo;
    }

    @Data
    public static class PriceBreakdown {
        private Money total;
    }

    @Data
    public static class Money {
        private String currencyCode;
        private long units;
    }
}