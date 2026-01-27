package com.example.JourneyMate.service.user;

import com.example.JourneyMate.entity.user.UsuarioEntity;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UsuarioEntity> findAll();

    UsuarioEntity findById(Integer idUsuario);

    UsuarioEntity save(UsuarioEntity usuarioEntity);

    void deleteById(Integer idUsuario);
}
