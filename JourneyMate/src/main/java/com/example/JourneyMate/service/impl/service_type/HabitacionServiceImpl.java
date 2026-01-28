package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.entity.service_type.HabitacionEntity;
import com.example.JourneyMate.service.service_type.HabitacionService;

import java.util.List;

public class HabitacionServiceImpl implements HabitacionService {
    @Override
    public List<HabitacionEntity> findAll() {
        return List.of();
    }

    @Override
    public HabitacionEntity findById(Integer idHabitacion) {
        return null;
    }

    @Override
    public HabitacionEntity save(HabitacionEntity habitacion) {
        return null;
    }

    @Override
    public void deleteById(Integer idHabitacion) {

    }

    @Override
    public List<HabitacionEntity> findByHotelIdServicio(Integer idHotel) {
        return List.of();
    }

    @Override
    public List<HabitacionEntity> findByCapacidadGreaterThanEqual(Integer capacidad) {
        return List.of();
    }
}
