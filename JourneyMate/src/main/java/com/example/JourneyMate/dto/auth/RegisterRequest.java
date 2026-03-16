package com.example.JourneyMate.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private String password;
    private String telefono;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

}
