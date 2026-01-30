package com.example.JourneyMate.service.impl.route;

import com.example.JourneyMate.dao.route.RutaRepository;
import com.example.JourneyMate.entity.route.RutaEntity;
import com.example.JourneyMate.service.route.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RutaServiceImpl implements RutaService {

    @Autowired
    private RutaRepository rutaRepository;

    @Override
    public List<RutaEntity> findByUsuarioIdUsuario(Integer idUsuario) {
        return rutaRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<RutaEntity> findAll() {
        return rutaRepository.findAll();
    }

    @Override
    public RutaEntity findById(Integer idRuta) {
        return rutaRepository.findById(idRuta) .orElse(null);
    }

    @Override
    public RutaEntity saveRuta(RutaEntity ruta) {
        return rutaRepository.save(ruta);
    }

    @Override
    public void deleteBy(Integer idRuta) {
        rutaRepository.deleteById(idRuta);
    }
}
