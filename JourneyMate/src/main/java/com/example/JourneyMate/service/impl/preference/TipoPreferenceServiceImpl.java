package com.example.JourneyMate.service.impl.preference;

import com.example.JourneyMate.dao.preference.TipoPreferenciaRepository;
import com.example.JourneyMate.entity.preference.TipoPreferenciaEntity;
import com.example.JourneyMate.service.preference.TipoPreferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TipoPreferenceServiceImpl implements TipoPreferenciaService {

    @Autowired
    private TipoPreferenciaRepository tipoPreferenciaRepository;

    @Override
    public List<TipoPreferenciaEntity> findAll() {
        return tipoPreferenciaRepository.findAll();
    }

    @Override
    public TipoPreferenciaEntity findById(Integer idTipoPreferencia) {
        return tipoPreferenciaRepository.findById(idTipoPreferencia).orElse(null);
    }

    @Override
    public TipoPreferenciaEntity findByNombre(String nombre) {
        return tipoPreferenciaRepository.findByName(nombre);
    }

    @Override
    public TipoPreferenciaEntity save(TipoPreferenciaEntity tipoPreferencia) {
        return tipoPreferenciaRepository.save(tipoPreferencia);
    }

    @Override
    public void deleteById(Integer idTipoPreferencia) {
        tipoPreferenciaRepository.deleteById(idTipoPreferencia);
    }
}
