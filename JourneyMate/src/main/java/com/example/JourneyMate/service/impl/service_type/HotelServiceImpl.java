package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.HotelRepository;
import com.example.JourneyMate.entity.service_type.HotelEntity;
import com.example.JourneyMate.service.service_type.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de hoteles.
 * Proporciona operaciones CRUD y consultas específicas como la búsqueda por estrellas.
 */
@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    /**
     * Obtiene los hoteles filtrados por número de estrellas.
     *
     * @param estrellas número de estrellas del hotel
     * @return lista de hoteles que coinciden con el filtro
     */
    @Override
    public List<HotelEntity> findByEstrellas(Integer estrellas) {
        return hotelRepository.findByEstrellas(estrellas);
    }

    /**
     * Obtiene todos los hoteles registrados.
     *
     * @return lista de hoteles
     */
    @Override
    public List<HotelEntity> findAll() {
        return hotelRepository.findAll();
    }

    /**
     * Busca un hotel por su identificador.
     *
     * @param idHotel identificador del hotel
     * @return la entidad encontrada
     * @throws RuntimeException si el hotel no existe
     */
    @Override
    public HotelEntity findById(Integer idHotel) {
        return hotelRepository.findById(idHotel)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
    }

    /**
     * Guarda o actualiza un hotel.
     *
     * @param hotel entidad a persistir
     * @return la entidad guardada
     */
    @Override
    public HotelEntity save(HotelEntity hotel) {
        return hotelRepository.save(hotel);
    }

    /**
     * Elimina un hotel por su identificador.
     *
     * @param idHotel identificador del hotel a eliminar
     */
    @Override
    public void deleteById(Integer idHotel) {
        hotelRepository.deleteById(idHotel);
    }
}