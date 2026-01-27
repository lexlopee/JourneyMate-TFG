package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.service_type.TrenEntity;

import java.util.List;

public interface TrenService {
    List<TrenEntity> findAll();

    TrenEntity findById(Integer idTren);

    TrenEntity save(TrenEntity trenEntity);

    void deleteById(Integer idTren);
}
