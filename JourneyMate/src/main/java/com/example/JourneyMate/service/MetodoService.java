package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.payment.MetodoEntity;

import java.util.List;

public interface MetodoService {
    List<MetodoEntity> findAll();

    MetodoEntity findById(Integer idMetodo);

    MetodoEntity save(MetodoEntity metodoEntity);

    void deleteById(Integer idMetodo);
}
