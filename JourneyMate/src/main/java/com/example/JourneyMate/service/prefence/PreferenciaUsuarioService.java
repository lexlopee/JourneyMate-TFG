package com.example.JourneyMate.service.prefence;

import com.example.JourneyMate.entity.preference.PreferenciaUsuarioEntity;

import java.util.List;

public interface PreferenciaUsuarioService {
    List<PreferenciaUsuarioEntity> findByUsuarioIdUsuario(Integer idUsuario);

    List<PreferenciaUsuarioEntity> finByAll();

    PreferenciaUsuarioEntity findById(Integer idUsuario);

    PreferenciaUsuarioEntity save(PreferenciaUsuarioEntity preferenciaUsuario);

    void deleteByHotelId(Integer idHotel);
}
