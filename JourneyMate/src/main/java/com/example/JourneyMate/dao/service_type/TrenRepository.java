package com.example.JourneyMate.dao.service_type;

import com.example.JourneyMate.entity.service_type.TrenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrenRepository extends JpaRepository<TrenEntity, Integer> {
}
