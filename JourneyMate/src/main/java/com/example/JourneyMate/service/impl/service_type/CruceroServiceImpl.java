package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.CruceroRepository;
import com.example.JourneyMate.entity.service_type.CruceroEntity;
import com.example.JourneyMate.service.service_type.CruceroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CruceroServiceImpl implements CruceroService {

    @Autowired
    private CruceroRepository cruceroRepository;

    @Override
    public List<CruceroEntity> findAll() {
        return cruceroRepository.findAll();
    }

    @Override
    public CruceroEntity findByIdCrucero(Integer idCrucero) {
        return cruceroRepository.findById(idCrucero).orElse(null);
    }

    @Override
    public CruceroEntity save(CruceroEntity crucero) {
        return cruceroRepository.save(crucero);
    }

    @Override
    public void deleteByIdCrucero(Integer idCrucero) {
        cruceroRepository.deleteById(idCrucero);
    }
}
