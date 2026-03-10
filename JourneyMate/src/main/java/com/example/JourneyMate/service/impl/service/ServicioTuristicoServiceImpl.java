package com.example.JourneyMate.service.impl.service;

import com.example.JourneyMate.dao.service.ServicioTuristicoRepository;
import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.service.service.ServicioTuristicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioTuristicoServiceImpl implements ServicioTuristicoService {

    private final ServicioTuristicoRepository servicioTuristicoRepository;

    @Override
    public List<ServicioTuristicoEntity> findAll() {
        return servicioTuristicoRepository.findAll();
    }

    @Override
    public ServicioTuristicoEntity findById(Integer idTuristico) {
        return servicioTuristicoRepository.findById(idTuristico)
                .orElseThrow(() ->
                        new RuntimeException("Servicio turístico no encontrado: " + idTuristico));
    }

    @Override
    public ServicioTuristicoEntity save(ServicioTuristicoEntity servicioTuristico) {
        return servicioTuristicoRepository.save(servicioTuristico);
    }

    @Override
    public void deleteById(Integer idTuristico) {
        servicioTuristicoRepository.deleteById(idTuristico);
    }
}