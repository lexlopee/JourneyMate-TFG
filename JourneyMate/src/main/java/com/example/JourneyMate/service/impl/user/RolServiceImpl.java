package com.example.JourneyMate.service.impl.user;

import com.example.JourneyMate.entity.user.RolEntity;
import com.example.JourneyMate.service.user.RolService;

import java.util.List;

public class RolServiceImpl implements RolService {
    @Override
    public List<RolEntity> findAll() {
        return List.of();
    }

    @Override
    public RolEntity findById(Integer idRol) {
        return null;
    }

    @Override
    public RolEntity findByRolName(String rolName) {
        return null;
    }

    @Override
    public List<RolEntity> findAllByRolName(String rolName) {
        return List.of();
    }

    @Override
    public RolEntity save(RolEntity rolEntity) {
        return null;
    }

    @Override
    public List<RolEntity> findAllByUserId(Integer idUser) {
        return List.of();
    }

    @Override
    public void deleteAllByUserId(Integer idUser) {

    }

    @Override
    public void deleteAllByRolId(Integer idRol) {

    }
}
