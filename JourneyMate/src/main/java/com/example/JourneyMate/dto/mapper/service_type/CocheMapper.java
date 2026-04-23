package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.coche.CocheRequestDTO;
import com.example.JourneyMate.dto.coche.CocheResponseDTO;
import com.example.JourneyMate.entity.service_type.CocheEntity;

public class CocheMapper {

    public static CocheEntity toEntity(CocheRequestDTO dto) {
        if (dto == null) return null;

        CocheEntity entity = new CocheEntity();

        // Ahora estos métodos transportan LocalDateTime
        entity.setHoraSalida(dto.getHoraSalida());
        entity.setHoraLlegada(dto.getHoraLlegada());

        entity.setPrecio(dto.getPrecio());
        entity.setDistancia(dto.getDistancia());
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());

        return entity;
    }

    public static CocheResponseDTO toDTO(CocheEntity entity) {
        if (entity == null) return null;

        CocheResponseDTO dto = new CocheResponseDTO();
        dto.setIdServicio(entity.getIdServicio());

        // Ahora estos métodos transportan LocalDateTime
        dto.setHoraSalida(entity.getHoraSalida());
        dto.setHoraLlegada(entity.getHoraLlegada());

        dto.setPrecio(entity.getPrecio());
        dto.setDistancia(entity.getDistancia());
        dto.setMarca(entity.getMarca());
        dto.setModelo(entity.getModelo());

        return dto;
    }
}