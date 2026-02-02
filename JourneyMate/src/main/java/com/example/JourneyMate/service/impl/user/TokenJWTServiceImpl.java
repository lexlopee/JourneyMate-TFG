package com.example.JourneyMate.service.impl.user;

import com.example.JourneyMate.dao.user.TokenJWTRepository;
import com.example.JourneyMate.entity.user.TokenJWTEntity;
import com.example.JourneyMate.service.user.TokenJWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TokenJWTServiceImpl implements TokenJWTService {

    @Autowired
    private TokenJWTRepository tokenJWTRepository;

    @Override
    public Optional<TokenJWTEntity> findByToken(String token) {
        return tokenJWTRepository.findByToken(token);
    }

    @Override
    public List<TokenJWTEntity> findByUsuario_IdUsuario(Integer idUsuario) {
        return tokenJWTRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<TokenJWTEntity> findByAll() {
        return tokenJWTRepository.findAll();
    }

    @Override
    public TokenJWTEntity findById(Integer idToken) {
        return tokenJWTRepository.findById(idToken).orElse(null);
    }

    @Override
    public TokenJWTEntity save(TokenJWTEntity tokenJWT) {
        return tokenJWTRepository.save(tokenJWT);
    }

    @Override
    public void deleteById(Integer idToken) {
        tokenJWTRepository.deleteById(idToken);
    }
}
