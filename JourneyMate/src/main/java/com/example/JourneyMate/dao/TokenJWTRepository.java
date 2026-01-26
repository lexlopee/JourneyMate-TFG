package com.example.JourneyMate.dao;

import com.example.JourneyMate.entity.user.TokenJWTEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJWTRepository extends JpaRepository<TokenJWTEntity,Integer> {
}
