package com.example.JourneyMate.external.cars;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {
    private String carName;      // Nombre del modelo (ej: Dodge Grand Caravan)
    private String imageUrl;     // URL de la foto del coche
    private Double price;        // Precio numérico (ej: 77.63)
    private String currency;     // Moneda (ej: EUR)
    private String vendorName;   // Empresa de alquiler (ej: Sixt, Payless)
    private String transmission; // Manual o Automatic
    private Integer seats;       // Número de plazas
    private Integer bags;        // Suma de maletas grandes + pequeñas
    private String deeplink;     // URL externa para completar la reserva (forward_url)
}