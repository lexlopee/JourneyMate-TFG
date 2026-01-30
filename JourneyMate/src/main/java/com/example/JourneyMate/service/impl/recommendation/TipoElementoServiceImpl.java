package com.example.JourneyMate.service.impl.recommendation;

import com.example.JourneyMate.dao.recommendation.TipoElementoRepository;
import com.example.JourneyMate.entity.recommendation.TipoElementoEntity;
import com.example.JourneyMate.service.recommendation.TipoElementoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TipoElementoServiceImpl implements TipoElementoService {

    @Autowired
    private TipoElementoRepository tipoElementoRepository;

    @Override
    public List<TipoElementoEntity> findAll() {
        return tipoElementoRepository.findAll();
    }

    @Override
    public TipoElementoEntity findById(Integer idTipoElemento) {
        return tipoElementoRepository.findById(idTipoElemento).orElse(null);
    }

    @Override
    public TipoElementoEntity save(TipoElementoEntity tipoElemento) {
        return tipoElementoRepository.save(tipoElemento);
    }

    @Override
    public void deleteById(Integer idTipoElemento) {
        tipoElementoRepository.deleteById(idTipoElemento);
    }
}
