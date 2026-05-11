package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.HotelEntity;
import com.example.JourneyMate.service.service_type.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar las operaciones CRUD
 * relacionadas con los hoteles del sistema.
 *
 * Permite listar, consultar, crear, actualizar, eliminar
 * y filtrar hoteles por número de estrellas.
 */
@RestController
@RequestMapping("/hoteles")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    /**
     * Obtiene la lista de todos los hoteles disponibles.
     *
     * @return lista de {@link HotelEntity}
     */
    @GetMapping
    public ResponseEntity<List<HotelEntity>> findAll() {
        return ResponseEntity.ok(hotelService.findAll());
    }

    /**
     * Obtiene un hotel por su identificador.
     *
     * @param id identificador del hotel
     * @return entidad {@link HotelEntity} si existe,
     *         o respuesta 404 si no se encuentra
     */
    @GetMapping("/{id}")
    public ResponseEntity<HotelEntity> findById(@PathVariable Integer id) {
        HotelEntity hotel = hotelService.findById(id);
        return hotel != null
                ? ResponseEntity.ok(hotel)
                : ResponseEntity.notFound().build();
    }

    /**
     * Crea un nuevo hotel en el sistema.
     *
     * @param hotel entidad del hotel a crear
     * @return hotel creado {@link HotelEntity}
     */
    @PostMapping
    public ResponseEntity<HotelEntity> create(@RequestBody HotelEntity hotel) {
        return ResponseEntity.ok(hotelService.save(hotel));
    }

    /**
     * Actualiza un hotel existente.
     *
     * Si el hotel no existe, devuelve una respuesta 404.
     *
     * @param id identificador del hotel a actualizar
     * @param hotel datos actualizados del hotel
     * @return hotel actualizado {@link HotelEntity} o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<HotelEntity> update(@PathVariable Integer id,
                                              @RequestBody HotelEntity hotel) {

        if (hotelService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        // porque hereda de ServicioTuristicoEntity
        hotel.setIdServicio(id);

        return ResponseEntity.ok(hotelService.save(hotel));
    }

    /**
     * Elimina un hotel por su identificador.
     *
     * Si el hotel no existe, devuelve 404.
     *
     * @param id identificador del hotel a eliminar
     * @return respuesta sin contenido si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        if (hotelService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        hotelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Filtra hoteles por número de estrellas.
     *
     * @param estrellas número de estrellas del hotel
     * @return lista de hoteles que coinciden con el filtro
     */
    @GetMapping("/estrellas/{estrellas}")
    public ResponseEntity<List<HotelEntity>> findByEstrellas(@PathVariable Integer estrellas) {
        return ResponseEntity.ok(hotelService.findByEstrellas(estrellas));
    }
}