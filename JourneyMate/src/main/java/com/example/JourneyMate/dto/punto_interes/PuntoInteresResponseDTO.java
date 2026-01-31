package com.example.JourneyMate.dto.punto_interes;

import lombok.Data;

@Data
public class PuntoInteresResponseDTO {

    private Integer idPunto;
    private String ciudad;
    private String nombre;
    private String descripcion;
    private Integer idCategoria;
    private Integer numeroRutas;
}
