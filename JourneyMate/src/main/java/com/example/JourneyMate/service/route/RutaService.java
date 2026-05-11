package com.example.JourneyMate.service.route;

import com.example.JourneyMate.entity.route.RutaEntity;

import java.util.List;

/**
 * Servicio para la gestión de rutas.
 * Define operaciones de consulta, persistencia y eliminación sobre {@link RutaEntity}.
 */
public interface RutaService {

    /**
     * Obtiene todas las rutas asociadas a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de rutas del usuario
     */
    List<RutaEntity> findByUsuarioIdUsuario(Integer idUsuario);

    /**
     * Obtiene todas las rutas registradas.
     *
     * @return lista de rutas
     */
    List<RutaEntity> findAll();

    /**
     * Busca una ruta por su identificador.
     *
     * @param idRuta identificador de la ruta
     * @return la ruta encontrada o null si no existe
     */
    RutaEntity findById(Integer idRuta);

    /**
     * Guarda o actualiza una ruta.
     *
     * @param ruta entidad de ruta
     * @return ruta persistida
     */
    RutaEntity saveRuta(RutaEntity ruta);

    /**
     * Elimina una ruta por su identificador.
     *
     * @param idRuta identificador de la ruta a eliminar
     */
    void deleteBy(Integer idRuta);
}