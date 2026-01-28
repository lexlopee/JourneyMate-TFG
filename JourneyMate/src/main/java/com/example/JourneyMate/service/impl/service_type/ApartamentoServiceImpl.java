package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.entity.service_type.ApartamentoEntity;
import com.example.JourneyMate.service.service_type.ApartamentoService;

import java.util.List;

public class ApartamentoServiceImpl implements ApartamentoService {
    @Override
    public List<ApartamentoEntity> findAll() {
        return List.of();
    }

    @Override
    public ApartamentoEntity findById(Integer idApartamento) {
        return null;
    }

    @Override
    public ApartamentoEntity save(ApartamentoEntity apartamento) {
        return null;
    }

    @Override
    public void deleteById(Integer idApartamento) {

    }
}
