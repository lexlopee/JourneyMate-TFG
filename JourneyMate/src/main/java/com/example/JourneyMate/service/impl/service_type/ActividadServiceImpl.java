package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.ActividadRepository;
import com.example.JourneyMate.entity.service_type.ActividadEntity;
import com.example.JourneyMate.service.service_type.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de actividades.
 * Proporciona operaciones CRUD sobre {@link ActividadEntity}.
 */
@Service
public class ActividadServiceImpl implements ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    /**
     * Obtiene todas las actividades registradas.
     *
     * @return lista de actividades
     */
    @Override
    public List<ActividadEntity> findAll() {
        return actividadRepository.findAll();
    }

    /**
     * Busca una actividad por su identificador.
     *
     * @param idActividad identificador de la actividad
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public ActividadEntity findByIdActividad(Integer idActividad) {
        return actividadRepository.findById(idActividad).orElse(null);
    }

    /**
     * Guarda o actualiza una actividad.
     *
     * @param actividad entidad a persistir
     * @return la entidad guardada
     */
    @Override
    public ActividadEntity saveActividad(ActividadEntity actividad) {
        return actividadRepository.save(actividad);
    }

    /**
     * Elimina una actividad por su identificador.
     *
     * @param idActividad identificador de la actividad a eliminar
     */
    @Override
    public void deleteByIdActividad(Integer idActividad) {
        actividadRepository.deleteById(idActividad);
    }
}