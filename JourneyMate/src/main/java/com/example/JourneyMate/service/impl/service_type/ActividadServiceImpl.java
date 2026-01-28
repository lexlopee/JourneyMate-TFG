package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.entity.service_type.ActividadEntity;
import com.example.JourneyMate.service.service_type.ActividadService;

import java.util.List;

public class ActividadServiceImpl implements ActividadService {
    @Override
    public List<ActividadEntity> findByAll() {
        return List.of();
    }

    @Override
    public ActividadEntity findByIdActividad(Integer idActividad) {
        return null;
    }

    @Override
    public ActividadEntity saveActivadad(ActividadEntity actividad) {
        return null;
    }

    @Override
    public void deleteByIdActividad(Integer idActividad) {

    }
}
