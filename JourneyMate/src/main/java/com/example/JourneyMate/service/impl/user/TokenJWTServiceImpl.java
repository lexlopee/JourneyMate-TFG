package com.example.JourneyMate.service.impl.user;

import com.example.JourneyMate.entity.user.TokenJWTEntity;
import com.example.JourneyMate.service.user.TokenJWTService;

import java.util.List;
import java.util.Optional;

public class TokenJWTServiceImpl implements TokenJWTService {
    @Override
    public Optional<TokenJWTEntity> findByToken(String token) {
        return Optional.empty();
    }

    @Override
    public List<TokenJWTEntity> findByUsuario_IdUsuario(Integer idUsuario) {
        return List.of();
    }

    @Override
    public List<TokenJWTEntity> findByAll() {
        return List.of();
    }

    @Override
    public TokenJWTEntity findById(Integer idToken) {
        return null;
    }

    @Override
    public TokenJWTEntity save(TokenJWTEntity tokenJWT) {
        return null;
    }

    @Override
    public void deleteById(Integer idToken) {

    }
}
