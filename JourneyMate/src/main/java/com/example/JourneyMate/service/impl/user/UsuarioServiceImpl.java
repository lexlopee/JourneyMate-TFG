package com.example.JourneyMate.service.impl.user;

import com.example.JourneyMate.entity.user.UsuarioEntity;
import com.example.JourneyMate.service.user.UsuarioService;

import java.util.List;
import java.util.Optional;

public class UsuarioServiceImpl implements UsuarioService {
    @Override
    public Optional<UsuarioEntity> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public List<UsuarioEntity> findAll() {
        return List.of();
    }

    @Override
    public UsuarioEntity findById(Integer idUsuario) {
        return null;
    }

    @Override
    public UsuarioEntity save(UsuarioEntity usuarioEntity) {
        return null;
    }

    @Override
    public void deleteById(Integer idUsuario) {

    }
}
