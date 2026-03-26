package com.example.JourneyMate.external.accommodations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelDetailsDTO {
    private boolean status;
    private String message;
    private DataPayload data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataPayload {

        @JsonProperty("hotel_name")
        private String hotelName;

        private String address;
        private String city;

        @JsonProperty("currency_code")
        private String currencyCode;

        @JsonProperty("accommodation_type_name")
        private String accommodationTypeName;

        @JsonProperty("review_score")
        private Double reviewScore;

        @JsonProperty("review_score_word")
        private String reviewScoreWord;

        @JsonProperty("review_nr")
        private int reviewNr;

        @JsonProperty("available_rooms")
        private int availableRooms;

        @JsonProperty("product_price_breakdown")
        private PriceBreakdown productPriceBreakdown;

        private Map<String, RoomDetails> rooms;

        @JsonProperty("facilities_block")
        private FacilitiesBlock facilitiesBlock;

        @JsonProperty("breakfast_review_score")
        private BreakfastScore breakfastReviewScore;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceBreakdown {

        @JsonProperty("all_inclusive_amount")
        private PriceValue allInclusiveAmount;

        @JsonProperty("gross_amount")
        private PriceValue grossAmount;

        @JsonProperty("excluded_amount")
        private PriceValue excludedAmount;

        @JsonProperty("discounted_amount")
        private PriceValue discountedAmount;

        @JsonProperty("nr_stays")
        private int nrStays;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceValue {
        private double value;
        private String currency;

        @JsonProperty("amount_rounded")
        private String amountRounded;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoomDetails {
        private List<Photo> photos;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photo {
        @JsonProperty("url_max750")
        private String urlMax750;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FacilitiesBlock {
        private List<Facility> facilities;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Facility {
        private String name;
        private String icon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BreakfastScore {
        private double rating;

        @JsonProperty("review_score_word")
        private String reviewScoreWord;
    }
}