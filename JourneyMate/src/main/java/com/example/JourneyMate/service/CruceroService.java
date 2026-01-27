package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.service_type.CruceroEntity;

import java.util.List;

public interface CruceroService {
    List<CruceroEntity> findAll();

    CruceroEntity finByIdCrucero(Integer idCrucero);

    CruceroEntity save(CruceroEntity crucero);

    void deleteByIdCrucero(Integer idCrucero);
}
