package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.vuelo.VueloRequestDTO;
import com.example.JourneyMate.dto.vuelo.VueloResponseDTO;
import com.example.JourneyMate.entity.service_type.VueloEntity;

public class VueloMapper {

    public static VueloEntity toEntity(VueloRequestDTO dto) {
        VueloEntity entity = new VueloEntity();
        entity.setCompania(dto.getCompania());
        entity.setFechaSalida(dto.getFechaSalida());
        entity.setFechaRegreso(dto.getFechaRegreso());
        entity.setOrigen(dto.getOrigen());
        entity.setDestino(dto.getDestino());
        return entity;
    }

    public static VueloResponseDTO toDTO(VueloEntity entity) {
        VueloResponseDTO dto = new VueloResponseDTO();
        dto.setIdServicio(entity.getIdServicio());
        dto.setCompania(entity.getCompania());
        dto.setFechaSalida(entity.getFechaSalida());
        dto.setFechaRegreso(entity.getFechaRegreso());
        dto.setOrigen(entity.getOrigen());
        dto.setDestino(entity.getDestino());
        return dto;
    }
}
