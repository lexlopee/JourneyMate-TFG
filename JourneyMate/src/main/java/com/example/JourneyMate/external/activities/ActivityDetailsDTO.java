package com.example.JourneyMate.external.activities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDetailsDTO {
    private String idActividad;
    private String nombre;
    private String descripcionLarga;
    private String shortDescription;
    private List<String> fotos;
    private Double precio;
    private String moneda;
    private List<String> whatsIncluded;
    private List<String> notIncluded;
    private boolean hasFreeCancellation;
    private Double averageRating;
    private Integer totalReviews;
}