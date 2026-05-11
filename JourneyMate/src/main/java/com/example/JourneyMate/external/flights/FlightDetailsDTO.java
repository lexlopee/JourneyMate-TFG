package com.example.JourneyMate.external.flights;

import lombok.Data;

import java.util.List;

/**
 * DTO que representa el detalle completo de un vuelo.
 * Incluye información de segmentos, precios, equipaje, emisiones y viajeros.
 */
@Data
public class FlightDetailsDTO {

    /**
     * Estado de la respuesta de la API.
     */
    private boolean status;

    /**
     * Mensaje asociado a la respuesta.
     */
    private String message;

    /**
     * Marca temporal de la respuesta.
     */
    private long timestamp;

    /**
     * Datos principales del vuelo.
     */
    private DataPayload data;

    /**
     * Contenedor principal con la información del vuelo.
     */
    @Data
    public static class DataPayload {

        /**
         * Token de referencia del vuelo.
         */
        private String token;

        /**
         * Lista de segmentos del vuelo (trayectos principales).
         */
        private List<Segment> segments;

        /**
         * Desglose de precios del vuelo.
         */
        private PriceBreakdown priceBreakdown;

        /**
         * Información sobre emisiones de carbono del vuelo.
         */
        private CarbonEmissions carbonEmissions;

        /**
         * Tipo de viaje (one-way, round-trip, etc.).
         */
        private String tripType;

        /**
         * Lista de pasajeros del vuelo.
         */
        private List<Traveller> travellers;
    }

    /**
     * Representa un segmento del vuelo (una parte del itinerario).
     */
    @Data
    public static class Segment {

        /**
         * Aeropuerto de salida.
         */
        private Airport departureAirport;

        /**
         * Aeropuerto de llegada.
         */
        private Airport arrivalAirport;

        /**
         * Hora de salida del segmento.
         */
        private String departureTime;

        /**
         * Hora de llegada del segmento.
         */
        private String arrivalTime;

        /**
         * Lista de tramos o legs dentro del segmento.
         */
        private List<Leg> legs;

        /**
         * Duración total del segmento en minutos.
         */
        private int totalTime;

        /**
         * Equipaje facturado por viajero.
         */
        private List<LuggageWrapper> travellerCheckedLuggage;

        /**
         * Equipaje de cabina por viajero.
         */
        private List<LuggageWrapper> travellerCabinLuggage;
    }

    /**
     * Representa un tramo individual del vuelo.
     */
    @Data
    public static class Leg {

        /**
         * Hora de salida del tramo.
         */
        private String departureTime;

        /**
         * Hora de llegada del tramo.
         */
        private String arrivalTime;

        /**
         * Aeropuerto de salida.
         */
        private Airport departureAirport;

        /**
         * Aeropuerto de llegada.
         */
        private Airport arrivalAirport;

        /**
         * Clase de cabina (economy, business, etc.).
         */
        private String cabinClass;

        /**
         * Información del vuelo operado.
         */
        private FlightInfo flightInfo;

        /**
         * Información de aerolíneas involucradas.
         */
        private List<CarrierData> carriersData;

        /**
         * Duración total del tramo en minutos.
         */
        private int totalTime;
    }

    /**
     * Información del vuelo operado.
     */
    @Data
    public static class FlightInfo {

        /**
         * Número de vuelo.
         */
        private int flightNumber;

        /**
         * Tipo de aeronave.
         */
        private String planeType;

        /**
         * Información del operador del vuelo.
         */
        private CarrierInfo carrierInfo;
    }

    /**
     * Información del operador del vuelo.
     */
    @Data
    public static class CarrierInfo {

        /**
         * Aerolínea operadora.
         */
        private String operatingCarrier;

        /**
         * Aerolínea comercializadora.
         */
        private String marketingCarrier;
    }

    /**
     * Representa un aeropuerto.
     */
    @Data
    public static class Airport {

        /**
         * Código IATA del aeropuerto.
         */
        private String code;

        /**
         * Nombre de la ciudad (formato completo).
         */
        private String cityName;

        /**
         * Ciudad del aeropuerto.
         */
        private String city;

        /**
         * País del aeropuerto.
         */
        private String country;

        /**
         * Nombre del aeropuerto.
         */
        private String name;
    }

    /**
     * Información de aerolínea.
     */
    @Data
    public static class CarrierData {

        /**
         * Nombre de la aerolínea.
         */
        private String name;

        /**
         * Código de la aerolínea.
         */
        private String code;

        /**
         * URL del logo de la aerolínea.
         */
        private String logo;
    }

    /**
     * Desglose de precios del vuelo.
     */
    @Data
    public static class PriceBreakdown {

        /**
         * Precio total.
         */
        private Money total;

        /**
         * Tarifa base.
         */
        private Money baseFare;

        /**
         * Tasas adicionales.
         */
        private Money fee;

        /**
         * Impuestos.
         */
        private Money tax;

        /**
         * Descuentos aplicados.
         */
        private Money discount;
    }

    /**
     * Representación monetaria de un valor.
     */
    @Data
    public static class Money {

        /**
         * Código de la moneda (EUR, USD, etc.).
         */
        private String currencyCode;

        /**
         * Parte entera del valor.
         */
        private long units;

        /**
         * Parte decimal del valor en nanos.
         */
        private long nanos;
    }

    /**
     * Información de equipaje asociado a un pasajero.
     */
    @Data
    public static class LuggageWrapper {

        /**
         * Referencia del viajero.
         */
        private String travellerReference;

        /**
         * Detalles de la franquicia de equipaje.
         */
        private LuggageAllowance luggageAllowance;
    }

    /**
     * Detalles de la franquicia de equipaje.
     */
    @Data
    public static class LuggageAllowance {

        /**
         * Tipo de equipaje (CHECKED_IN o HAND).
         */
        private String luggageType;

        /**
         * Número máximo de piezas permitidas.
         */
        private int maxPiece;

        /**
         * Peso máximo por pieza.
         */
        private double maxWeightPerPiece;

        /**
         * Unidad de medida del peso.
         */
        private String massUnit;
    }

    /**
     * Información sobre emisiones de carbono del vuelo.
     */
    @Data
    public static class CarbonEmissions {

        /**
         * Huella de carbono del vuelo.
         */
        private Footprint footprintForOffer;
    }

    /**
     * Representa la huella de carbono.
     */
    @Data
    public static class Footprint {

        /**
         * Cantidad de emisiones.
         */
        private double quantity;

        /**
         * Unidad de medida de las emisiones.
         */
        private String unit;
    }

    /**
     * Información de un pasajero.
     */
    @Data
    public static class Traveller {

        /**
         * Referencia del pasajero.
         */
        private String travellerReference;

        /**
         * Tipo de pasajero (adult, child, infant, etc.).
         */
        private String type;

        /**
         * Edad del pasajero.
         */
        private Integer age;
    }
}