package com.example.JourneyMate.service.user;

import com.example.JourneyMate.entity.user.TokenJWTEntity;

import java.util.List;
import java.util.Optional;

public interface TokenJWTService {
    Optional<TokenJWTEntity> findByToken(String token);

    List<TokenJWTEntity> findByUsuario_IdUsuario(Integer idUsuario);

    List<TokenJWTEntity> findByAll();

    TokenJWTEntity findById(Integer idToken);

    TokenJWTEntity save(TokenJWTEntity tokenJWT);

    void deleteById(Integer idToken);

}
