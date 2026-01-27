package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.recommendation.RecomendacionEntity;

import java.util.List;

public interface RecomendacionService {
    List<RecomendacionEntity> findByIdUsuario(Integer idUsuario);
    List<RecomendacionEntity> findByAll();
    RecomendacionEntity findById(Integer idRecomendacion);
    RecomendacionEntity save(RecomendacionEntity recomendacion);
    void deleteById(Integer idRecomendacion);
}
