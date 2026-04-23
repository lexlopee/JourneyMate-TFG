package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.CocheRepository;
import com.example.JourneyMate.entity.service_type.CocheEntity;
import com.example.JourneyMate.service.service_type.CocheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CocheServiceImpl implements CocheService {

    @Autowired
    private CocheRepository cocheRepository;

    @Override
    public List<CocheEntity> findAll() {
        return cocheRepository.findAll();
    }

    @Override
    public CocheEntity findById(Integer idVTC) {
        return cocheRepository.findById(idVTC).orElse(null);
    }

    @Override
    public CocheEntity save(CocheEntity cocheEntity) {
        return cocheRepository.save(cocheEntity);
    }

    @Override
    public void deleteById(Integer idVTC) {
        cocheRepository.deleteById(idVTC);
    }
}
