package com.example.JourneyMate.external.cruises;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CruiseDTO {
    // Información General
    private String id;
    private String nombreCrucero;
    private String nombreBarco;
    private String logoBarco;

    // Logística
    private String fechaSalida;
    private String fechaLlegada;
    private Integer noches;
    private String puertoSalida;
    private String destino;

    // Precios y Moneda
    private Double precioDesde;
    private Double tasasYImpuestos;
    private String moneda;
    private String linkReserva;

    // Multimedia
    private String imagenPrincipal;
    private List<String> galeriaImagenes;

    // Detalles del Itinerario (Paradas día a día)
    private List<ItinerarioDTO> paradas;

    // Tipos de cabinas (Interior, Balcón, etc.)
    private List<CabinaDTO> cabinas;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ItinerarioDTO {
        private Integer dia;
        private String puerto;
        private String region;
        private String llegada;
        private String salida;
        private String tipo; // "PORT" o "CRUISING"
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CabinaDTO {
        private String tipo; // Interior, Balcony, etc.
        private String descripcion;
        private Double precio;
        private List<String> amenidades;
    }
}