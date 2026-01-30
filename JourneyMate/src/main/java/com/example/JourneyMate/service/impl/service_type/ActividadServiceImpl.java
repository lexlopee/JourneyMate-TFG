package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.ActividadRepository;
import com.example.JourneyMate.entity.service_type.ActividadEntity;
import com.example.JourneyMate.service.service_type.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActividadServiceImpl implements ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    @Override
    public List<ActividadEntity> findAll() {
        return actividadRepository.findAll();
    }

    @Override
    public ActividadEntity findByIdActividad(Integer idActividad) {
        return actividadRepository.findById(idActividad).orElse(null);
    }

    @Override
    public ActividadEntity saveActividad(ActividadEntity actividad) {
        return actividadRepository.save(actividad);
    }

    @Override
    public void deleteByIdActividad(Integer idActividad) {
        actividadRepository.deleteById(idActividad);
    }
}
