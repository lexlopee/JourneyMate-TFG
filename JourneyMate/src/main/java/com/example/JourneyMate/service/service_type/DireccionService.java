package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.DireccionEntity;

import java.util.List;

/**
 * Servicio para la gestión de direcciones.
 * Define operaciones CRUD básicas sobre {@link DireccionEntity}.
 */
public interface DireccionService {

    /**
     * Obtiene todas las direcciones registradas.
     *
     * @return lista de direcciones
     */
    List<DireccionEntity> findAll();

    /**
     * Busca una dirección por su identificador.
     *
     * @param idDireccion identificador de la dirección
     * @return la dirección encontrada o null si no existe
     */
    DireccionEntity findById(Integer idDireccion);

    /**
     * Guarda o actualiza una dirección.
     *
     * @param direccion entidad de dirección
     * @return dirección persistida
     */
    DireccionEntity save(DireccionEntity direccion);

    /**
     * Elimina una dirección por su identificador.
     *
     * @param idDireccion identificador de la dirección a eliminar
     */
    void deleteById(Integer idDireccion);
}