package com.example.JourneyMate.dao.preference;

import com.example.JourneyMate.entity.preference.PreferenciaUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferenciaUsuarioRepository extends JpaRepository<PreferenciaUsuarioEntity, Integer> {
    List<PreferenciaUsuarioEntity> findByUsuarioIdUsuario(Integer idUsuario);
}

