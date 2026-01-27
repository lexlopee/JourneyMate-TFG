package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.VueloEntity;

import java.util.List;

public interface VueloService {
    List<VueloEntity> findAll();

    VueloEntity findById(Integer idVuelo);

    VueloEntity save(VueloEntity vueloEntity);

    void deleteById(Integer idVuelo);
}
