package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.VTCRepository;
import com.example.JourneyMate.entity.service_type.VTCEntity;
import com.example.JourneyMate.service.service_type.VTCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VTCServiceImpl implements VTCService {

    @Autowired
    private VTCRepository vtcRepository;

    @Override
    public List<VTCEntity> findAll() {
        return vtcRepository.findAll();
    }

    @Override
    public VTCEntity findById(Integer idVTC) {
        return vtcRepository.findById(idVTC).orElse(null);
    }

    @Override
    public VTCEntity save(VTCEntity vtcEntity) {
        return vtcRepository.save(vtcEntity);
    }

    @Override
    public void deleteById(Integer idVTC) {
        vtcRepository.deleteById(idVTC);
    }
}
