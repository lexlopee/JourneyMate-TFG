package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.service_type.VTCEntity;

import java.util.List;

public interface VTCService {
    List<VTCEntity> findAll();

    VTCEntity findById(Integer idVTC);

    VTCEntity save(VTCEntity vtcEntity);

    void deleteById(Integer idVTC);
}
