package com.example.JourneyMate.dto.usuario;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioResponseDTO {

    private Integer idUsuario;
    private String telefono;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaRegistro;
    private LocalDate fechaNacimiento;
    private String email;
    private Integer idRol;
}
