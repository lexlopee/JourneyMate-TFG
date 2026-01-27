package com.example.JourneyMate.service.service;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;

import java.util.List;


public interface ServicioTuristicoService {
    List<ServicioTuristicoEntity> findByAll();

    ServicioTuristicoEntity findById(Integer idTuristico);

    ServicioTuristicoEntity save(ServicioTuristicoEntity servicioTuristico);

    void deleteById(Integer idTuristico);

}
