package com.example.JourneyMate.service.recommendation;

import com.example.JourneyMate.entity.recommendation.RecomendacionEntity;

import java.util.List;

public interface RecomendacionService {
    List<RecomendacionEntity> findByIdUsuario(Integer idUsuario);

    List<RecomendacionEntity> findAll();

    RecomendacionEntity findById(Integer idRecomendacion);

    RecomendacionEntity save(RecomendacionEntity recomendacion);

    void deleteById(Integer idRecomendacion);
}
