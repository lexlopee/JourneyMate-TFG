package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.VueloEntity;

import java.util.List;

/**
 * Servicio para la gestión de vuelos.
 * Define operaciones CRUD básicas sobre {@link VueloEntity}.
 */
public interface VueloService {

    /**
     * Obtiene todos los vuelos registrados.
     *
     * @return lista de vuelos
     */
    List<VueloEntity> findAll();

    /**
     * Busca un vuelo por su identificador.
     *
     * @param idVuelo identificador del vuelo
     * @return el vuelo encontrado o null si no existe
     */
    VueloEntity findById(Integer idVuelo);

    /**
     * Guarda o actualiza un vuelo.
     *
     * @param vueloEntity entidad de vuelo
     * @return vuelo persistido
     */
    VueloEntity save(VueloEntity vueloEntity);

    /**
     * Elimina un vuelo por su identificador.
     *
     * @param idVuelo identificador del vuelo a eliminar
     */
    void deleteById(Integer idVuelo);
}