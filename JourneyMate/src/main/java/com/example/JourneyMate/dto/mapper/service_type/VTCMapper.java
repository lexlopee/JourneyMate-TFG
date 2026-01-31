package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.vtc.VTCRequestDTO;
import com.example.JourneyMate.dto.vtc.VTCResponseDTO;
import com.example.JourneyMate.entity.service_type.VTCEntity;

public class VTCMapper {

    public static VTCEntity toEntity(VTCRequestDTO dto) {
        VTCEntity entity = new VTCEntity();
        entity.setHoraSalida(dto.getHoraSalida());
        entity.setHoraLlegada(dto.getHoraLlegada());
        entity.setPrecio(dto.getPrecio());
        entity.setDistancia(dto.getDistancia());
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());
        return entity;
    }

    public static VTCResponseDTO toDTO(VTCEntity entity) {
        VTCResponseDTO dto = new VTCResponseDTO();
        dto.setIdServicio(entity.getIdServicio());
        dto.setHoraSalida(entity.getHoraSalida());
        dto.setHoraLlegada(entity.getHoraLlegada());
        dto.setPrecio(entity.getPrecio());
        dto.setDistancia(entity.getDistancia());
        dto.setMarca(entity.getMarca());
        dto.setModelo(entity.getModelo());
        return dto;
    }
}
