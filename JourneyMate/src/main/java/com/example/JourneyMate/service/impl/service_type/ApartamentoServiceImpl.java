package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service.ServicioTuristicoRepository;
import com.example.JourneyMate.dao.service_type.ApartamentoRepository;
import com.example.JourneyMate.entity.service_type.ApartamentoEntity;
import com.example.JourneyMate.service.service_type.ApartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartamentoServiceImpl implements ApartamentoService {

    @Autowired
    private ApartamentoRepository apartamentoRepository;

    @Override
    public List<ApartamentoEntity> findAll() {
        return apartamentoRepository.findAll();
    }

    @Override
    public ApartamentoEntity findById(Integer idApartamento) {
        return apartamentoRepository.findById(idApartamento).orElse(null);
    }

    @Override
    public ApartamentoEntity save(ApartamentoEntity apartamento) {
        return apartamentoRepository.save(apartamento);
    }

    @Override
    public void deleteById(Integer idApartamento) {
        apartamentoRepository.deleteById(idApartamento);
    }
}
