package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.service_type.ApartamentoEntity;

import java.util.List;

public interface ApartamentoService {
    List<ApartamentoEntity> findAll();

    ApartamentoEntity findById(Integer idApartamento);

    ApartamentoEntity save(ApartamentoEntity apartamento);

    void deleteById(Integer idApartamento);
}
