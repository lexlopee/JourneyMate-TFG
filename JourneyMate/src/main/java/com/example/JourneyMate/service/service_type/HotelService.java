package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.HotelEntity;

import java.util.List;

public interface HotelService {
    List<HotelEntity> findByEstrellas(Integer estrellas);

    List<HotelEntity> findAll();

    HotelEntity findById(Integer idHotel);

    HotelEntity save(HotelEntity hotel);

    void deleteById(Integer idHotel);
}
