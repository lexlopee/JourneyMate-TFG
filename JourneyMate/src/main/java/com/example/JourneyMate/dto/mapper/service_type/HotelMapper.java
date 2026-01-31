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

        // Evitamos enviar la lista completa de habitaciones
        if (entity.getHabitaciones() != null) {
            dto.setNumeroHabitaciones(entity.getHabitaciones().size());
        } else {
            dto.setNumeroHabitaciones(0);
        }

        return dto;
    }
}
