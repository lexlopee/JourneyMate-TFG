package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.VTCEntity;

import java.util.List;

public interface VTCService {
    List<VTCEntity> findAll();

    VTCEntity findById(Integer idVTC);

    VTCEntity save(VTCEntity vtcEntity);

    void deleteById(Integer idVTC);
}
