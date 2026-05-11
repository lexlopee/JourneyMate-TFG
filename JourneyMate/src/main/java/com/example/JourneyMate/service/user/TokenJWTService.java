package com.example.JourneyMate.service.user;

import com.example.JourneyMate.entity.user.TokenJWTEntity;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de tokens JWT.
 * Proporciona operaciones de consulta, persistencia y eliminación de tokens
 * asociados a usuarios del sistema.
 */
public interface TokenJWTService {

    /**
     * Busca un token JWT por su valor.
     *
     * @param token valor del token JWT
     * @return Optional con el token si existe
     */
    Optional<TokenJWTEntity> findByToken(String token);

    /**
     * Obtiene todos los tokens asociados a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de tokens del usuario
     */
    List<TokenJWTEntity> findByUsuario_IdUsuario(Integer idUsuario);

    /**
     * Obtiene todos los tokens registrados.
     *
     * @return lista de tokens JWT
     */
    List<TokenJWTEntity> findByAll();

    /**
     * Busca un token por su identificador.
     *
     * @param idToken identificador del token
     * @return el token encontrado o null si no existe
     */
    TokenJWTEntity findById(Integer idToken);

    /**
     * Guarda o actualiza un token JWT.
     *
     * @param tokenJWT entidad de token
     * @return token persistido
     */
    TokenJWTEntity save(TokenJWTEntity tokenJWT);

    /**
     * Elimina un token JWT por su identificador.
     *
     * @param idToken identificador del token a eliminar
     */
    void deleteById(Integer idToken);
}