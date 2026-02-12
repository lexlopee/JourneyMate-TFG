package com.example.JourneyMate.service.preference;

import com.example.JourneyMate.entity.preference.PreferenciaUsuarioEntity;

import java.util.List;

public interface PreferenciaUsuarioService {
    List<PreferenciaUsuarioEntity> findByUsuarioIdUsuario(Integer idUsuario);

    List<PreferenciaUsuarioEntity> finByAll();

    PreferenciaUsuarioEntity findById(Integer idPreferencia);

    PreferenciaUsuarioEntity save(PreferenciaUsuarioEntity preferenciaUsuario);

    void deleteById(Integer idPreferencia);
}
