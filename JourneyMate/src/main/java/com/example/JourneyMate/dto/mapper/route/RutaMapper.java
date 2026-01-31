package com.example.JourneyMate.dto.mapper.route;

import com.example.JourneyMate.dto.ruta.RutaRequestDTO;
import com.example.JourneyMate.dto.ruta.RutaResponseDTO;
import com.example.JourneyMate.entity.route.RutaEntity;
import com.example.JourneyMate.entity.user.UsuarioEntity;

public class RutaMapper {

    public static RutaEntity toEntity(RutaRequestDTO dto) {
        RutaEntity entity = new RutaEntity();

        entity.setNombre(dto.getNombre());
        entity.setFecha_creacion(dto.getFechaCreacion());

        // Relación con Usuario
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(dto.getIdUsuario());
        entity.setUsuario(usuario);

        return entity;
    }

    public static RutaResponseDTO toDTO(RutaEntity entity) {
        RutaResponseDTO dto = new RutaResponseDTO();

        dto.setIdRuta(entity.getIdRuta());
        dto.setNombre(entity.getNombre());
        dto.setFechaCreacion(entity.getFecha_creacion());
        dto.setIdUsuario(entity.getUsuario().getIdUsuario());

        if (entity.getPuntos() != null) {
            dto.setNumeroPuntos(entity.getPuntos().size());
        } else {
            dto.setNumeroPuntos(0);
        }

        return dto;
    }
}
