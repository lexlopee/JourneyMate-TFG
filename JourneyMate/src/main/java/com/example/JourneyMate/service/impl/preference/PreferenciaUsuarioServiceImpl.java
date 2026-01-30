package com.example.JourneyMate.service.impl.preference;

import com.example.JourneyMate.dao.preference.PreferenciaUsuarioRepository;
import com.example.JourneyMate.entity.preference.PreferenciaUsuarioEntity;
import com.example.JourneyMate.service.preference.PreferenciaUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PreferenciaUsuarioServiceImpl implements PreferenciaUsuarioService {

    @Autowired
    private PreferenciaUsuarioRepository preferenciaUsuarioRepository;

    @Override
    public List<PreferenciaUsuarioEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return preferenciaUsuarioRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    public List<PreferenciaUsuarioEntity> finByAll() {
        return preferenciaUsuarioRepository.findAll();
    }

    @Override
    public PreferenciaUsuarioEntity findById(Integer idPreferncia) {
        return preferenciaUsuarioRepository.findById(idPreferncia).orElse(null);
    }

    @Override
    public PreferenciaUsuarioEntity save(PreferenciaUsuarioEntity preferenciaUsuario) {
        return preferenciaUsuarioRepository.save(preferenciaUsuario);
    }

    @Override
    public void deleteByHotelId(Integer idPreferncia) {
        preferenciaUsuarioRepository.deleteById(idPreferncia);
    }
}
