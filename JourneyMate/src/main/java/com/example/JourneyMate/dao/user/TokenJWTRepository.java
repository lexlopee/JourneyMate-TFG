package com.example.JourneyMate.dao.user;

import com.example.JourneyMate.entity.user.TokenJWTEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenJWTRepository extends JpaRepository<TokenJWTEntity, Integer> {
    Optional<TokenJWTEntity> findByToken(String token);

    List<TokenJWTEntity> findByUsuario_IdUsuario(Integer idUsuario);

}
