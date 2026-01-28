package com.example.JourneyMate.service.impl.preference;

import com.example.JourneyMate.entity.preference.PreferenciaUsuarioEntity;
import com.example.JourneyMate.service.preference.PreferenciaUsuarioService;

import java.util.List;

public class PreferenciaUsuarioServiceImpl implements PreferenciaUsuarioService {
    @Override
    public List<PreferenciaUsuarioEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return List.of();
    }

    @Override
    public List<PreferenciaUsuarioEntity> finByAll() {
        return List.of();
    }

    @Override
    public PreferenciaUsuarioEntity findById(Integer idUsuario) {
        return null;
    }

    @Override
    public PreferenciaUsuarioEntity save(PreferenciaUsuarioEntity preferenciaUsuario) {
        return null;
    }

    @Override
    public void deleteByHotelId(Integer idHotel) {

    }
}
