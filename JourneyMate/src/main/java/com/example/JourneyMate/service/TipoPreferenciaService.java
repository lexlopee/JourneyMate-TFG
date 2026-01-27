package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.preference.TipoPreferenciaEntity;

import java.util.List;

public interface TipoPreferenciaService {
    List<TipoPreferenciaEntity> findAll();
    TipoPreferenciaEntity findById(Integer idTipoPreferencia);
    TipoPreferenciaEntity findByNombre(String nombre);
    TipoPreferenciaEntity save (TipoPreferenciaEntity tipoPreferencia);
    void deleteById(Integer idTipoPreferencia);

}
