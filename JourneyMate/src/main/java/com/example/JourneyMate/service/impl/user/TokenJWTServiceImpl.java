package com.example.JourneyMate.service.impl.user;

import com.example.JourneyMate.dao.user.TokenJWTRepository;
import com.example.JourneyMate.entity.user.TokenJWTEntity;
import com.example.JourneyMate.service.user.TokenJWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para la gestión de tokens JWT.
 * Proporciona operaciones para consultar, guardar y eliminar tokens
 * asociados a usuarios del sistema.
 */
@Service
public class TokenJWTServiceImpl implements TokenJWTService {

    @Autowired
    private TokenJWTRepository tokenJWTRepository;

    /**
     * Busca un token JWT por su valor.
     *
     * @param token valor del token JWT
     * @return Optional con el token si existe
     */
    @Override
    public Optional<TokenJWTEntity> findByToken(String token) {
        return tokenJWTRepository.findByToken(token);
    }

    /**
     * Obtiene todos los tokens asociados a un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return lista de tokens del usuario
     */
    @Override
    public List<TokenJWTEntity> findByUsuario_IdUsuario(Integer idUsuario) {
        return tokenJWTRepository.findByUsuario_IdUsuario(idUsuario);
    }

    /**
     * Obtiene todos los tokens registrados en el sistema.
     *
     * @return lista de tokens JWT
     */
    @Override
    public List<TokenJWTEntity> findByAll() {
        return tokenJWTRepository.findAll();
    }

    /**
     * Busca un token por su identificador.
     *
     * @param idToken identificador del token
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public TokenJWTEntity findById(Integer idToken) {
        return tokenJWTRepository.findById(idToken).orElse(null);
    }

    /**
     * Guarda o actualiza un token JWT.
     *
     * @param tokenJWT entidad de token
     * @return token persistido
     */
    @Override
    public TokenJWTEntity save(TokenJWTEntity tokenJWT) {
        return tokenJWTRepository.save(tokenJWT);
    }

    /**
     * Elimina un token JWT por su identificador.
     *
     * @param idToken identificador del token a eliminar
     */
    @Override
    public void deleteById(Integer idToken) {
        tokenJWTRepository.deleteById(idToken);
    }
}