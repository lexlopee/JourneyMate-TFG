package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.HabitacionRepository;
import com.example.JourneyMate.entity.service_type.HabitacionEntity;
import com.example.JourneyMate.service.service_type.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitacionServiceImpl implements HabitacionService {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Override
    public List<HabitacionEntity> findAll() {
        return habitacionRepository.findAll();
    }

    @Override
    public HabitacionEntity findById(Integer idHabitacion) {
        return habitacionRepository.findById(idHabitacion).orElse(null);
    }

    @Override
    public HabitacionEntity save(HabitacionEntity habitacion) {
        return habitacionRepository.save(habitacion);
    }

    @Override
    public void deleteById(Integer idHabitacion) {
        habitacionRepository.deleteById(idHabitacion);
    }

    @Override
    public List<HabitacionEntity> findByHotel_IdServicio(Integer idHotel) {
        return habitacionRepository.findByHotel_IdServicio(idHotel);
    }

    @Override
    public List<HabitacionEntity> findByCapacidadGreaterThanEqual(Integer capacidad) {
        return habitacionRepository.findByCapacidadGreaterThanEqual(capacidad);
    }
}
