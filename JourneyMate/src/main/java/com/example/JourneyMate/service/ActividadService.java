package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.service_type.ActividadEntity;

import java.util.List;

public interface ActividadService {
    List<ActividadEntity> findByAll();

    ActividadEntity findByIdActividad(Integer idActividad);

    ActividadEntity saveActivadad(ActividadEntity actividad);

    void deleteByIdActividad(Integer idActividad);


}
