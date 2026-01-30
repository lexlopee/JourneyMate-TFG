package com.example.JourneyMate.service.impl.payment;

import com.example.JourneyMate.dao.payment.MetodoRepository;
import com.example.JourneyMate.entity.payment.MetodoEntity;
import com.example.JourneyMate.service.payment.MetodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetodoServiceImpl implements MetodoService {

    @Autowired
    private MetodoRepository metodoRepository;

    @Override
    public List<MetodoEntity> findAll() {
        return metodoRepository.findAll();
    }

    @Override
    public MetodoEntity findById(Integer idMetodo) {
        return metodoRepository.findById(idMetodo).orElse(null);
    }

    @Override
    public MetodoEntity save(MetodoEntity metodoEntity) {
        return metodoRepository.save(metodoEntity);
    }

    @Override
    public void deleteById(Integer idMetodo) {
        metodoRepository.deleteById(idMetodo);
    }
}
