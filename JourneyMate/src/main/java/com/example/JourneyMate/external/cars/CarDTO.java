package com.example.JourneyMate.external.cars;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la información de un coche de alquiler.
 * Se utiliza para mostrar opciones de vehículos disponibles en el sistema.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {

    /**
     * Nombre del modelo del coche (por ejemplo: Dodge Grand Caravan).
     */
    private String carName;

    /**
     * URL de la imagen del vehículo.
     */
    private String imageUrl;

    /**
     * Precio del alquiler del coche.
     */
    private Double price;

    /**
     * Código de la moneda del precio (EUR, USD, etc.).
     */
    private String currency;

    /**
     * Empresa de alquiler del vehículo (por ejemplo: Sixt, Payless).
     */
    private String vendorName;

    /**
     * Tipo de transmisión del vehículo (Manual o Automatic).
     */
    private String transmission;

    /**
     * Número de plazas disponibles en el coche.
     */
    private Integer seats;

    /**
     * Capacidad total de equipaje (maletas grandes + pequeñas).
     */
    private Integer bags;

    /**
     * Enlace externo para completar la reserva del vehículo.
     */
    private String deeplink;
}