package com.example.JourneyMate.entity.preference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tipo_preferencia", schema = "journeymate")

public class TipoPreferenciaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_preferencia")
    private Integer idTipoPreferencia;
    private String name;
}
