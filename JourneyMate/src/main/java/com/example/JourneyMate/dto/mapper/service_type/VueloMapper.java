package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.vuelo.VueloRequestDTO;
import com.example.JourneyMate.dto.vuelo.VueloResponseDTO;
import com.example.JourneyMate.entity.service_type.VueloEntity;

public class VueloMapper {

    public static VueloEntity toEntity(VueloRequestDTO dto) {
        VueloEntity entity = new VueloEntity();
        entity.setCompañia(dto.getCompañia());
        entity.setFechaSalida(dto.getFechaSalida());
        entity.setFechaLlegada(dto.getFechaLlegada());
        entity.setOrigen(dto.getOrigen());
        entity.setDestino(dto.getDestino());
        return entity;
    }

    public static VueloResponseDTO toDTO(VueloEntity entity) {
        VueloResponseDTO dto = new VueloResponseDTO();
        dto.setIdServicio(entity.getIdServicio());
        dto.setCompañia(entity.getCompañia());
        dto.setFechaSalida(entity.getFechaSalida());
        dto.setFechaLlegada(entity.getFechaLlegada());
        dto.setOrigen(entity.getOrigen());
        dto.setDestino(entity.getDestino());
        return dto;
    }
}
