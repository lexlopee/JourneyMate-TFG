package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.entity.service_type.HotelEntity;
import com.example.JourneyMate.service.service_type.HotelService;

import java.util.List;

public class HotelServiceImpl implements HotelService {
    @Override
    public List<HotelEntity> findByEstrellas(Integer estrellas) {
        return List.of();
    }

    @Override
    public List<HotelEntity> findByAll() {
        return List.of();
    }

    @Override
    public HotelEntity findById(Integer idHotel) {
        return null;
    }

    @Override
    public HotelEntity save(HotelEntity hotel) {
        return null;
    }

    @Override
    public void deleteById(Integer idHotel) {

    }
}
