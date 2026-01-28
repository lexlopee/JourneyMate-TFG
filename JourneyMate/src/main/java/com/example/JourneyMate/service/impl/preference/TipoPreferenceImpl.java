package com.example.JourneyMate.service.impl.preference;

import com.example.JourneyMate.entity.preference.TipoPreferenciaEntity;
import com.example.JourneyMate.service.preference.TipoPreferenciaService;

import java.util.List;

public class TipoPreferenceImpl implements TipoPreferenciaService {
    @Override
    public List<TipoPreferenciaEntity> findAll() {
        return List.of();
    }

    @Override
    public TipoPreferenciaEntity findById(Integer idTipoPreferencia) {
        return null;
    }

    @Override
    public TipoPreferenciaEntity findByNombre(String nombre) {
        return null;
    }

    @Override
    public TipoPreferenciaEntity save(TipoPreferenciaEntity tipoPreferencia) {
        return null;
    }

    @Override
    public void deleteById(Integer idTipoPreferencia) {

    }
}
