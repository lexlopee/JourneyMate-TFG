package com.example.JourneyMate.dto.mapper.interest;

import com.example.JourneyMate.dto.punto_interes.PuntoInteresRequestDTO;
import com.example.JourneyMate.dto.punto_interes.PuntoInteresResponseDTO;
import com.example.JourneyMate.entity.interest.CategoriaEntity;
import com.example.JourneyMate.entity.interest.PuntoInteresEntity;

public class PuntoInteresMapper {

    public static PuntoInteresEntity toEntity(PuntoInteresRequestDTO dto) {
        PuntoInteresEntity entity = new PuntoInteresEntity();

        entity.setCiudad(dto.getCiudad());
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());

        // Relación con categoría
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setIdCategoria(dto.getIdCategoria());
        entity.setCategoria(categoria);

        return entity;
    }

    public static PuntoInteresResponseDTO toDTO(PuntoInteresEntity entity) {
        PuntoInteresResponseDTO dto = new PuntoInteresResponseDTO();

        dto.setIdPunto(entity.getIdPunto());
        dto.setCiudad(entity.getCiudad());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setIdCategoria(entity.getCategoria().getIdCategoria());

        if (entity.getRutas() != null) {
            dto.setNumeroRutas(entity.getRutas().size());
        } else {
            dto.setNumeroRutas(0);
        }

        return dto;
    }
}
