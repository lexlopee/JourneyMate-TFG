package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.service_type.ActividadEntity;

import java.util.List;

public interface ActividadService {
    List<ActividadEntity> findByAll();
    ActividadEntity findById(Integer id);
    ActividadEntity save(ActividadEntity actividad);
    void deleteById(Integer id);
}
