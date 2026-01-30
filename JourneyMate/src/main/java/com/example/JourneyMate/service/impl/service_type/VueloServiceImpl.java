package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.VueloRepository;
import com.example.JourneyMate.entity.service_type.VueloEntity;
import com.example.JourneyMate.service.service_type.VueloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VueloServiceImpl implements VueloService {

    @Autowired
    private VueloRepository vueloRepository;

    @Override
    public List<VueloEntity> findAll() {
        return vueloRepository.findAll();
    }

    @Override
    public VueloEntity findById(Integer idVuelo) {
        return vueloRepository.findById(idVuelo).orElse(null);
    }

    @Override
    public VueloEntity save(VueloEntity vueloEntity) {
        return vueloRepository.save(vueloEntity);
    }

    @Override
    public void deleteById(Integer idVuelo) {
        vueloRepository.deleteById(idVuelo);
    }
}
