package com.example.JourneyMate.external.flights;

import lombok.Data;
import java.util.List;

@Data
public class FlightDetailsDTO {
    private boolean status;
    private String message;
    private long timestamp;
    private DataPayload data;

    @Data
    public static class DataPayload {
        private String token;
        private List<Segment> segments;
        private PriceBreakdown priceBreakdown;
        private CarbonEmissions carbonEmissions;
        private String tripType;
        private List<Traveller> travellers;
    }

    @Data
    public static class Segment {
        private Airport departureAirport;
        private Airport arrivalAirport;
        private String departureTime;
        private String arrivalTime;
        private List<Leg> legs;
        private int totalTime;
        private List<LuggageWrapper> travellerCheckedLuggage;
        private List<LuggageWrapper> travellerCabinLuggage;
    }

    @Data
    public static class Leg {
        private String departureTime;
        private String arrivalTime;
        private Airport departureAirport;
        private Airport arrivalAirport;
        private String cabinClass;
        private FlightInfo flightInfo;
        private List<CarrierData> carriersData;
        private int totalTime;
    }

    @Data
    public static class FlightInfo {
        private int flightNumber;
        private String planeType;
        private CarrierInfo carrierInfo;
    }

    @Data
    public static class CarrierInfo {
        private String operatingCarrier;
        private String marketingCarrier;
    }

    @Data
    public static class Airport {
        private String code;
        private String cityName;
        private String city;
        private String country;
        private String name;
    }

    @Data
    public static class CarrierData {
        private String name;
        private String code;
        private String logo;
    }

    @Data
    public static class PriceBreakdown {
        private Money total;
        private Money baseFare;
        private Money fee;
        private Money tax;
        private Money discount;
    }

    @Data
    public static class Money {
        private String currencyCode;
        private long units;
        private long nanos;
    }

    @Data
    public static class LuggageWrapper {
        private String travellerReference;
        private LuggageAllowance luggageAllowance;
    }

    @Data
    public static class LuggageAllowance {
        private String luggageType; // CHECKED_IN o HAND
        private int maxPiece;
        private double maxWeightPerPiece;
        private String massUnit;
    }

    @Data
    public static class CarbonEmissions {
        private Footprint footprintForOffer;
    }

    @Data
    public static class Footprint {
        private double quantity;
        private String unit;
    }

    @Data
    public static class Traveller {
        private String travellerReference;
        private String type;
        private Integer age;
    }
}