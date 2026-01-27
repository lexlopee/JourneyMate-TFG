package com.example.JourneyMate.dao.user;

import com.example.JourneyMate.entity.user.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<RolEntity, Integer> {
    Optional<RolEntity> findByNombre(String nombre);
}
