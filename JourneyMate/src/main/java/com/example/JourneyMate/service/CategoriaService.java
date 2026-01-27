package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.interest.CategoriaEntity;

import java.util.List;

public interface CategoriaService {
    List<CategoriaEntity> findAll();

    CategoriaEntity findById(Integer idCategoria);

    CategoriaEntity save(CategoriaEntity categoria);

    void deleteById(Integer idCategoria);
}
