package com.example.JourneyMate.service.user;

import com.example.JourneyMate.entity.user.UsuarioEntity;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios.
 * Proporciona operaciones de consulta, validación, persistencia y eliminación
 * de usuarios en el sistema.
 */
public interface UsuarioService {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo electrónico del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioEntity> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email indicado.
     *
     * @param email correo electrónico a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return lista de usuarios
     */
    List<UsuarioEntity> findAll();

    /**
     * Busca un usuario por su identificador.
     *
     * @param idUsuario identificador del usuario
     * @return el usuario encontrado o null si no existe
     */
    UsuarioEntity findById(Integer idUsuario);

    /**
     * Guarda o actualiza un usuario.
     *
     * @param usuarioEntity entidad de usuario
     * @return usuario persistido
     */
    UsuarioEntity save(UsuarioEntity usuarioEntity);

    /**
     * Elimina un usuario por su identificador.
     *
     * @param idUsuario identificador del usuario a eliminar
     */
    void deleteById(Integer idUsuario);
}