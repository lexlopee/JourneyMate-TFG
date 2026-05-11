package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.ActividadEntity;

import java.util.List;

/**
 * Servicio para la gestión de actividades.
 * Define operaciones CRUD básicas sobre {@link ActividadEntity}.
 */
public interface ActividadService {

    /**
     * Obtiene todas las actividades registradas.
     *
     * @return lista de actividades
     */
    List<ActividadEntity> findAll();

    /**
     * Busca una actividad por su identificador.
     *
     * @param idActividad identificador de la actividad
     * @return la actividad encontrada o null si no existe
     */
    ActividadEntity findByIdActividad(Integer idActividad);

    /**
     * Guarda o actualiza una actividad.
     *
     * @param actividad entidad de actividad
     * @return actividad persistida
     */
    ActividadEntity saveActividad(ActividadEntity actividad);

    /**
     * Elimina una actividad por su identificador.
     *
     * @param idActividad identificador de la actividad a eliminar
     */
    void deleteByIdActividad(Integer idActividad);
}