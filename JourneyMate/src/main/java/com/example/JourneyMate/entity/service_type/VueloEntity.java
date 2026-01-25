package com.example.JourneyMate.entity.service_type;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Table(name = "vuelo", schema = "journeymate")
@Data
@Entity

public class VueloEntity {
    private String compañia;
    private LocalDate fechaLlegada;
    private String origen;
    private LocalDate fechaSalida;
    private String destino;

}
