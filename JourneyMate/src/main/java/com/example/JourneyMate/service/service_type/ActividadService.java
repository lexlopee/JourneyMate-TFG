package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.ActividadEntity;

import java.util.List;

public interface ActividadService {
    List<ActividadEntity> findAll();

    ActividadEntity findByIdActividad(Integer idActividad);

    ActividadEntity saveActividad(ActividadEntity actividad);

    void deleteByIdActividad(Integer idActividad);


}
