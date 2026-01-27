package com.example.JourneyMate.service;

import com.example.JourneyMate.entity.user.RolEntity;

import java.util.List;

public interface RolService {
    List<RolEntity> findAll();

    RolEntity findById(Integer idRol);

    RolEntity findByRolName(String rolName);

    List<RolEntity> findAllByRolName(String rolName);

    RolEntity save(RolEntity rolEntity);

    List<RolEntity> findAllByUserId(Integer idUser);

    void deleteAllByUserId(Integer idUser);

    void deleteAllByRolId(Integer idRol);

}
