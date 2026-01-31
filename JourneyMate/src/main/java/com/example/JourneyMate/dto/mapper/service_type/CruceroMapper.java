package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.crucero.CruceroRequestDTO;
import com.example.JourneyMate.dto.crucero.CruceroResponseDTO;
import com.example.JourneyMate.entity.service_type.CruceroEntity;

public class CruceroMapper {

    public static CruceroEntity toEntity(CruceroRequestDTO dto) {
        CruceroEntity entity = new CruceroEntity();
        entity.setPuertoLlegada(dto.getPuertoLlegada());
        entity.setPuertoSalida(dto.getPuertoSalida());
        entity.setNaviera(dto.getNaviera());
        entity.setFechaSalida(dto.getFechaSalida());
        entity.setFechaLlegada(dto.getFechaLlegada());
        return entity;
    }

    public static CruceroResponseDTO toDTO(CruceroEntity entity) {
        CruceroResponseDTO dto = new CruceroResponseDTO();
        dto.setIdServicio(entity.getIdServicio());
        dto.setPuertoLlegada(entity.getPuertoLlegada());
        dto.setPuertoSalida(entity.getPuertoSalida());
        dto.setNaviera(entity.getNaviera());
        dto.setFechaSalida(entity.getFechaSalida());
        dto.setFechaLlegada(entity.getFechaLlegada());
        return dto;
    }
}
