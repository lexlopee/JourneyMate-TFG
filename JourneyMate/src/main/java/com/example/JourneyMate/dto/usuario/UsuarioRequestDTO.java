package com.example.JourneyMate.dto.usuario;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioRequestDTO {

    private String telefono;
    private String contrasenia;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaRegistro;
    private LocalDate fechaNacimiento;
    private String email;
    private Integer idRol;
}
