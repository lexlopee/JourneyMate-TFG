package com.example.JourneyMate.service.user;

import com.example.JourneyMate.entity.user.RolEntity;

import java.util.List;

/**
 * Servicio para la gestión de roles.
 * Define operaciones de consulta, persistencia y gestión de relaciones
 * entre usuarios y roles.
 */
public interface RolService {

    /**
     * Obtiene todos los roles registrados.
     *
     * @return lista de roles
     */
    List<RolEntity> findAll();

    /**
     * Busca un rol por su identificador.
     *
     * @param idRol identificador del rol
     * @return el rol encontrado o null si no existe
     */
    RolEntity findById(Integer idRol);

    /**
     * Busca un rol por su nombre.
     *
     * @param rolName nombre del rol
     * @return el rol encontrado o null si no existe
     */
    RolEntity findByRolName(String rolName);

    /**
     * Obtiene una lista de roles filtrados por nombre.
     *
     * @param rolName nombre del rol
     * @return lista de roles que coinciden con el nombre
     */
    List<RolEntity> findAllByRolName(String rolName);

    /**
     * Guarda o actualiza un rol.
     *
     * @param rolEntity entidad de rol
     * @return rol persistido
     */
    RolEntity save(RolEntity rolEntity);

    /**
     * Obtiene todos los roles asociados a un usuario.
     *
     * @param idUser identificador del usuario
     * @return lista de roles del usuario
     */
    List<RolEntity> findAllByUserId(Integer idUser);

    /**
     * Elimina todas las asociaciones entre un usuario y sus roles.
     *
     * @param idUser identificador del usuario
     */
    void deleteAllByUserId(Integer idUser);

    /**
     * Elimina un rol por su identificador.
     *
     * @param idRol identificador del rol
     */
    void deleteAllByRolId(Integer idRol);
}