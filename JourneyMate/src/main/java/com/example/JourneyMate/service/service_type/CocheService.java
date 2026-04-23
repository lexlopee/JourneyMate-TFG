package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.CocheEntity;

import java.util.List;

public interface CocheService {
    List<CocheEntity> findAll();

    CocheEntity findById(Integer idVTC);

    CocheEntity save(CocheEntity cocheEntity);

    void deleteById(Integer idVTC);
}
