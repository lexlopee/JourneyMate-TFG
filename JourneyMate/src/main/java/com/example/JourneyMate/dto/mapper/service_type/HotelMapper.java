package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.hotel.HotelRequestDTO;
import com.example.JourneyMate.dto.hotel.HotelResponseDTO;
import com.example.JourneyMate.entity.service_type.HotelEntity;

public class HotelMapper {

    public static HotelEntity toEntity(HotelRequestDTO dto) {
        HotelEntity entity = new HotelEntity();
        entity.setEstrellas(dto.getEstrellas());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }

    public static HotelResponseDTO toDTO(HotelEntity entity) {
        HotelResponseDTO dto = new HotelResponseDTO();
        dto.setIdServicio(entity.getIdServicio());
        dto.setEstrellas(entity.getEstrellas());
        dto.setDescripcion(entity.getDescripcion());

        return dto;
    }
}
