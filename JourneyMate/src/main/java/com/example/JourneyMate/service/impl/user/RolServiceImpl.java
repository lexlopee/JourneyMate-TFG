package com.example.JourneyMate.service.impl.user;

import com.example.JourneyMate.dao.user.RolRepository;
import com.example.JourneyMate.entity.user.RolEntity;
import com.example.JourneyMate.service.user.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

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
        return rolRepository.findByNombre(rolName)
                .map(List::of)
                .orElse(List.of());
    }
    @Override
    public RolEntity save(RolEntity rolEntity) {
        return rolRepository.save(rolEntity);
    }

    @Override
    public List<RolEntity> findAllByUserId(Integer idUser) {
        return rolRepository.findByUsuarios_IdUsuario(idUser);
    }

    @Override
    public void deleteAllByUserId(Integer idUser) {
        var roles = rolRepository.findByUsuarios_IdUsuario(idUser);

        roles.forEach(rol -> {
            rol.getUsuarios().removeIf(u -> u.getIdUsuario().equals(idUser));
            rolRepository.save(rol);
        });
    }
    @Override
    public void deleteAllByRolId(Integer idRol) {
        rolRepository.deleteById(idRol);
    }
}
