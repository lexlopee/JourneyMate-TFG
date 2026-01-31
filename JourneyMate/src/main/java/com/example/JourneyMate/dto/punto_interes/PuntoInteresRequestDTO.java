package com.example.JourneyMate.dto.punto_interes;

import lombok.Data;

@Data
public class PuntoInteresRequestDTO {

    private String ciudad;
    private String nombre;
    private String descripcion;
    private Integer idCategoria;
}
