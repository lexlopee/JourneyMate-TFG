package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.actividad.ActividadRequestDTO;
import com.example.JourneyMate.dto.actividad.ActividadResponseDTO;
import com.example.JourneyMate.entity.service_type.ActividadEntity;

public class ActividadMapper {

    public static ActividadEntity toEntity(ActividadRequestDTO dto) {
        ActividadEntity entity = new ActividadEntity();
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }

    public static ActividadResponseDTO toDTO(ActividadEntity entity) {
        ActividadResponseDTO dto = new ActividadResponseDTO();
        dto.setIdServicio(entity.getIdServicio());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }
}
