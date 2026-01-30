package com.example.JourneyMate.dao.preference;

import com.example.JourneyMate.entity.preference.TipoPreferenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPreferenciaRepository extends JpaRepository<TipoPreferenciaEntity, Integer> {
    TipoPreferenciaEntity findByName(String name);
}
