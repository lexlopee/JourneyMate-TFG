package com.example.JourneyMate.service.service_type;

import com.example.JourneyMate.entity.service_type.HotelEntity;

import java.util.List;

/**
 * Servicio para la gestión de hoteles.
 * Define operaciones de consulta, filtrado y mantenimiento sobre {@link HotelEntity}.
 */
public interface HotelService {

    /**
     * Obtiene los hoteles filtrados por número de estrellas.
     *
     * @param estrellas número de estrellas del hotel
     * @return lista de hoteles que coinciden con el filtro
     */
    List<HotelEntity> findByEstrellas(Integer estrellas);

    /**
     * Obtiene todos los hoteles registrados.
     *
     * @return lista de hoteles
     */
    List<HotelEntity> findAll();

    /**
     * Busca un hotel por su identificador.
     *
     * @param idHotel identificador del hotel
     * @return el hotel encontrado o null si no existe
     */
    HotelEntity findById(Integer idHotel);

    /**
     * Guarda o actualiza un hotel.
     *
     * @param hotel entidad de hotel
     * @return hotel persistido
     */
    HotelEntity save(HotelEntity hotel);

    /**
     * Elimina un hotel por su identificador.
     *
     * @param idHotel identificador del hotel a eliminar
     */
    void deleteById(Integer idHotel);
}