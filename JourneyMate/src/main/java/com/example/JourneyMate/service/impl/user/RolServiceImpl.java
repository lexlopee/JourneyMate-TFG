package com.example.JourneyMate.service.impl.user;

import com.example.JourneyMate.dao.user.RolRepository;
import com.example.JourneyMate.entity.user.RolEntity;
import com.example.JourneyMate.service.user.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<RolEntity> findAll() {
        return rolRepository.findAll();
    }

    @Override
    public RolEntity findById(Integer idRol) {
        return rolRepository.findById(idRol).orElse(null);
    }

    @Override
    public RolEntity findByRolName(String rolName) {
        return rolRepository.findByNombre(rolName).orElse(null);
    }

    @Override
    public List<RolEntity> findAllByRolName(String rolName) {
        return rolRepository.findAll()
                .stream()
                .filter(r -> r.getNombre().equalsIgnoreCase(rolName))
                .toList();
    }

    @Override
    public RolEntity save(RolEntity rolEntity) {
        return rolRepository.save(rolEntity);
    }

    @Override
    public List<RolEntity> findAllByUserId(Integer idUser) {
        return rolRepository.findAll()
                .stream()
                .filter(r -> r.getId_usuarios()
                        .stream()
                        .anyMatch(u -> u.getIdUsuario().equals(idUser)))
                .toList();
    }

    @Override
    public void deleteAllByUserId(Integer idUser) {
        rolRepository.findAll().forEach(rol -> {
            rol.getId_usuarios().removeIf(u -> u.getIdUsuario().equals(idUser));
            rolRepository.save(rol);
        });
    }

    @Override
    public void deleteAllByRolId(Integer idRol) {
        rolRepository.deleteById(idRol);
    }
}
