package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.HotelRepository;
import com.example.JourneyMate.entity.service_type.HotelEntity;
import com.example.JourneyMate.service.service_type.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public List<HotelEntity> findByEstrellas(Integer estrellas) {
        return hotelRepository.findByEstrellas(estrellas);
    }

    @Override
    public List<HotelEntity> findAll() {
        return hotelRepository.findAll();
    }

    @Override
    public HotelEntity findById(Integer idHotel) {
        return hotelRepository.findById(idHotel).orElse(null);
    }

    @Override
    public HotelEntity save(HotelEntity hotel) {
        return hotelRepository.save(hotel);
    }

    @Override
    public void deleteById(Integer idHotel) {
        hotelRepository.deleteById(idHotel);
    }
}
