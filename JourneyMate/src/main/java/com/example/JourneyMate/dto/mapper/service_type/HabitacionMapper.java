package com.example.JourneyMate.dto.mapper.service_type;

import com.example.JourneyMate.dto.habitacion.HabitacionRequestDTO;
import com.example.JourneyMate.dto.habitacion.HabitacionResponseDTO;
import com.example.JourneyMate.entity.service_type.HabitacionEntity;
import com.example.JourneyMate.entity.service_type.HotelEntity;

public class HabitacionMapper {

    public static HabitacionEntity toEntity(HabitacionRequestDTO dto) {
        HabitacionEntity entity = new HabitacionEntity();

        HotelEntity hotel = new HotelEntity();
        hotel.setIdServicio(dto.getIdHotel()); // IMPORTANTE: HotelEntity hereda de ServicioTuristicoEntity

        entity.setHotel(hotel);
        entity.setTipo(dto.getTipo());
        entity.setPrecioNoche(dto.getPrecioNoche());
        entity.setCapacidad(dto.getCapacidad());

        return entity;
    }

    public static HabitacionResponseDTO toDTO(HabitacionEntity entity) {
        HabitacionResponseDTO dto = new HabitacionResponseDTO();

        dto.setIdHabitacion(entity.getIdHabitacion());
        dto.setIdHotel(entity.getHotel().getIdServicio());
        dto.setTipo(entity.getTipo());
        dto.setPrecioNoche(entity.getPrecioNoche());
        dto.setCapacidad(entity.getCapacidad());

        return dto;
    }
}
