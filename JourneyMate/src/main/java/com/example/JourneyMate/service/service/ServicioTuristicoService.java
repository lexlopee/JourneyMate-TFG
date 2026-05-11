package com.example.JourneyMate.service.service;

import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;

import java.util.List;

/**
 * Servicio para la gestión de servicios turísticos.
 * Define operaciones CRUD básicas sobre {@link ServicioTuristicoEntity}.
 */
public interface ServicioTuristicoService {

    /**
     * Obtiene todos los servicios turísticos registrados.
     *
     * @return lista de servicios turísticos
     */
    List<ServicioTuristicoEntity> findAll();

    /**
     * Busca un servicio turístico por su identificador.
     *
     * @param idTuristico identificador del servicio turístico
     * @return la entidad encontrada o null si no existe
     */
    ServicioTuristicoEntity findById(Integer idTuristico);

    /**
     * Guarda o actualiza un servicio turístico.
     *
     * @param servicioTuristico entidad de servicio turístico
     * @return entidad persistida
     */
    ServicioTuristicoEntity save(ServicioTuristicoEntity servicioTuristico);

    /**
     * Elimina un servicio turístico por su identificador.
     *
     * @param idTuristico identificador del servicio a eliminar
     */
    void deleteById(Integer idTuristico);
}