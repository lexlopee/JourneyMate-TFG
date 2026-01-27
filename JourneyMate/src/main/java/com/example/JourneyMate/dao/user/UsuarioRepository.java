package com.example.JourneyMate.dao.user;

import com.example.JourneyMate.entity.user.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
