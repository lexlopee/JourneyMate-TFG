package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.tren.TrenRequestDTO;
import com.example.JourneyMate.dto.tren.TrenResponseDTO;
import com.example.JourneyMate.entity.service_type.TrenEntity;

public class TrenMapper {

    public static TrenEntity toEntity(TrenRequestDTO dto) {
        TrenEntity entity = new TrenEntity();
        entity.setFechaSalida(dto.getFechaSalida());
        entity.setFechaLlegada(dto.getFechaLlegada());
        entity.setCompañia(dto.getCompañia());
        entity.setOrigen(dto.getOrigen());
        entity.setDestino(dto.getDestino());
        return entity;
    }

    public static TrenResponseDTO toDTO(TrenEntity entity) {
        TrenResponseDTO dto = new TrenResponseDTO();
        dto.setIdServicio(entity.getIdServicio());
        dto.setFechaSalida(entity.getFechaSalida());
        dto.setFechaLlegada(entity.getFechaLlegada());
        dto.setCompañia(entity.getCompañia());
        dto.setOrigen(entity.getOrigen());
        dto.setDestino(entity.getDestino());
        return dto;
    }
}
