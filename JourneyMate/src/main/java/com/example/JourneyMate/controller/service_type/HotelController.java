package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.HotelEntity;
import com.example.JourneyMate.service.service_type.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hoteles")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<HotelEntity>> findAll() {
        return ResponseEntity.ok(hotelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelEntity> findById(@PathVariable Integer id) {
        HotelEntity hotel = hotelService.findById(id);
        return hotel != null ? ResponseEntity.ok(hotel) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<HotelEntity> create(@RequestBody HotelEntity hotel) {
        return ResponseEntity.ok(hotelService.save(hotel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelEntity> update(@PathVariable Integer id, @RequestBody HotelEntity hotel) {
        if (hotelService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        hotel.setIdServicio(id); // porque hereda de ServicioTuristicoEntity
        return ResponseEntity.ok(hotelService.save(hotel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (hotelService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        hotelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 🔍 Filtro: hoteles por número de estrellas
    @GetMapping("/estrellas/{estrellas}")
    public ResponseEntity<List<HotelEntity>> findByEstrellas(@PathVariable Integer estrellas) {
        return ResponseEntity.ok(hotelService.findByEstrellas(estrellas));
    }
}
