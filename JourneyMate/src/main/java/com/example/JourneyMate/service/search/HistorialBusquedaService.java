package com.example.JourneyMate.service.search;

import com.example.JourneyMate.entity.search.HistorialBusquedaEntity;

import java.util.List;

/**
 * Servicio para la gestión del historial de búsquedas.
 * Define operaciones de consulta, persistencia y eliminación sobre {@link HistorialBusquedaEntity}.
 */
public interface HistorialBusquedaService {

    /**
     * Obtiene el historial de búsquedas asociado a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de búsquedas realizadas por el usuario
     */
    List<HistorialBusquedaEntity> findByUsuarioIdUsuario(Integer idUsuario);

    /**
     * Obtiene todo el historial de búsquedas registrado.
     *
     * @return lista completa del historial de búsquedas
     */
    List<HistorialBusquedaEntity> findAll();

    /**
     * Busca una entrada del historial por su identificador.
     *
     * @param idHistorial identificador del registro
     * @return la entidad encontrada o null si no existe
     */
    HistorialBusquedaEntity findById(Integer idHistorial);

    /**
     * Guarda o actualiza una entrada del historial de búsquedas.
     *
     * @param historial entidad de historial
     * @return entidad persistida
     */
    HistorialBusquedaEntity save(HistorialBusquedaEntity historial);

    /**
     * Elimina una entrada del historial por su identificador.
     *
     * @param idHistorial identificador del registro a eliminar
     */
    void deleteById(Integer idHistorial);
}