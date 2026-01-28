package com.example.JourneyMate.service.impl.service;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.service.service.ServicioTuristicoService;

import java.util.List;

public class ServicioTuristicoServiceImpl implements ServicioTuristicoService {
    @Override
    public List<ServicioTuristicoEntity> findByAll() {
        return List.of();
    }

    @Override
    public ServicioTuristicoEntity findById(Integer idTuristico) {
        return null;
    }

    @Override
    public ServicioTuristicoEntity save(ServicioTuristicoEntity servicioTuristico) {
        return null;
    }

    @Override
    public void deleteById(Integer idTuristico) {

    }
}
