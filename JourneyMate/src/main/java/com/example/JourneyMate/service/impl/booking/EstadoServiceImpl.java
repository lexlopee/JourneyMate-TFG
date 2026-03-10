package com.example.JourneyMate.service.impl.booking;

import com.example.JourneyMate.dao.booking.EstadoRepository;
import com.example.JourneyMate.entity.booking.EstadoEntity;
import com.example.JourneyMate.service.booking.EstadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstadoServiceImpl implements EstadoService {

    private final EstadoRepository estadoRepository;

    @Override
    public EstadoEntity findByNombre(String nombre) {
        return estadoRepository
                .findByNombreIgnoreCase(nombre)
                .orElse(null);
    }

    @Override
    public List<EstadoEntity> findAll() {
        return estadoRepository.findAll();
    }

    @Override
    public Optional<EstadoEntity> findById(Integer idEstado) {
        return estadoRepository.findById(idEstado);
    }

    @Override
    public EstadoEntity crear(EstadoEntity estado) {
        return estadoRepository.save(estado);
    }

    @Override
    public EstadoEntity actualizar(Integer idEstado, EstadoEntity estado) {
        return estadoRepository.findById(idEstado)
                .map(e -> {
                    estado.setIdEstado(idEstado);
                    return estadoRepository.save(estado);
                })
                .orElse(null);
    }

    @Override
    public void deleteById(Integer idEstado) {
        estadoRepository.deleteById(idEstado);
    }

    @Override
    public boolean existsById(Integer idEstado) {
        return estadoRepository.existsById(idEstado);
    }
}

