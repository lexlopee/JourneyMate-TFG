package com.example.JourneyMate.entity.preference;

import com.example.JourneyMate.entity.user.UsuarioEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "preferencias_usuario", schema = "journeymate")
@Data
public class PreferenciaUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_preferencia;

    private Integer valor;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "id_tipo_preferencia")
    private TipoPreferenciaEntity tipoPreferencia;
}
