package com.example.JourneyMate.controller.interest;

import com.example.JourneyMate.entity.interest.PuntoInteresEntity;
import com.example.JourneyMate.service.interest.PuntoInteresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador encargado de gestionar los puntos de interés
 * dentro de la aplicación JourneyMate.
 * <p>
 * Permite realizar operaciones CRUD sobre los puntos turísticos
 * o lugares destacados registrados en el sistema.
 */
@RestController
@RequestMapping("/puntos")
public class PuntoInteresController {

    /**
     * Servicio encargado de la lógica de negocio
     * relacionada con puntos de interés.
     */
    @Autowired
    private PuntoInteresService puntoInteresService;

    /**
     * Obtiene todos los puntos de interés registrados.
     *
     * @return lista completa de puntos de interés
     */
    @GetMapping
    public ResponseEntity<List<PuntoInteresEntity>> findAll() {

        return ResponseEntity.ok(
                puntoInteresService.findAll()
        );
    }

    /**
     * Busca un punto de interés por su identificador.
     *
     * @param id identificador del punto de interés
     * @return punto encontrado o respuesta 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<PuntoInteresEntity> findById(
            @PathVariable Integer id) {

        PuntoInteresEntity punto =
                puntoInteresService.findById(id);

        return punto != null
                ? ResponseEntity.ok(punto)
                : ResponseEntity.notFound().build();
    }

    /**
     * Crea un nuevo punto de interés.
     *
     * @param punto información del punto de interés
     * @return punto de interés creado
     */
    @PostMapping
    public ResponseEntity<PuntoInteresEntity> create(
            @RequestBody PuntoInteresEntity punto) {

        return ResponseEntity.ok(
                puntoInteresService.save(punto)
        );
    }

    /**
     * Actualiza la información de un punto de interés existente.
     *
     * @param id identificador del punto de interés
     * @param punto nueva información del punto de interés
     * @return punto actualizado o respuesta 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<PuntoInteresEntity> update(
            @PathVariable Integer id,
            @RequestBody PuntoInteresEntity punto) {

        if (puntoInteresService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        punto.setIdPunto(id);

        return ResponseEntity.ok(
                puntoInteresService.save(punto)
        );
    }

    /**
     * Elimina un punto de interés por su identificador.
     *
     * @param id identificador del punto de interés
     * @return respuesta vacía si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id) {

        if (puntoInteresService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        puntoInteresService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
