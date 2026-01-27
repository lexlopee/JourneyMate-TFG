package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.recommendation.TipoElementoEntity;

import java.util.List;

public interface TipoElementoService {
   List<TipoElementoEntity> findAll();
   TipoElementoEntity findById(Integer idTipoElemento);
   TipoElementoEntity save(TipoElementoEntity tipoElemento);
   void deleteByIdTipoElemento(Integer idTipoElemento);
}
