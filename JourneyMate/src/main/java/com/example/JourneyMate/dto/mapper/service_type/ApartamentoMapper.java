package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.apartamento.ApartamentoRequestDTO;
import com.example.JourneyMate.dto.apartamento.ApartamentoResponseDTO;
import com.example.JourneyMate.entity.service_type.ApartamentoEntity;

public class ApartamentoMapper {

    public static ApartamentoEntity toEntity(ApartamentoRequestDTO dto) {
        ApartamentoEntity entity = new ApartamentoEntity();
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }

    public static ApartamentoResponseDTO toDTO(ApartamentoEntity entity) {
        ApartamentoResponseDTO dto = new ApartamentoResponseDTO();
        dto.setIdServicio(entity.getIdServicio());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }
}
