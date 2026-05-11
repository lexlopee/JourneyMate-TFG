package com.example.JourneyMate.controller.service;

import com.example.JourneyMate.dto.service.ServicioTuristicoRequestDTO;
import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.service.service.ServicioTuristicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar las operaciones CRUD
 * relacionadas con los servicios turísticos.
 *
 * Expone endpoints para crear, consultar, actualizar y eliminar
 * servicios turísticos dentro del sistema.
 */
@RestController
@RequestMapping("/api/v1/servicios")
@RequiredArgsConstructor
public class ServicioTuristicoController {

    private final ServicioTuristicoService servicioTuristicoService;

    /**
     * Obtiene la lista de todos los servicios turísticos disponibles.
     *
     * @return lista de {@link ServicioTuristicoEntity}
     */
    @GetMapping
    public ResponseEntity<List<ServicioTuristicoEntity>> findAll() {
        return ResponseEntity.ok(servicioTuristicoService.findAll());
    }

    /**
     * Obtiene un servicio turístico por su identificador.
     *
     * @param id identificador del servicio turístico
     * @return entidad {@link ServicioTuristicoEntity} correspondiente
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServicioTuristicoEntity> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(servicioTuristicoService.findById(id));
    }

    /**
     * Crea un nuevo servicio turístico.
     *
     * Nota: algunos campos como descripción, tipo o estrellas
     * han sido eliminados de la entidad base y solo aplican
     * a subclases específicas.
     *
     * @param dto datos del servicio turístico a crear
     * @return entidad creada {@link ServicioTuristicoEntity}
     */
    @PostMapping
    public ResponseEntity<ServicioTuristicoEntity> create(@RequestBody ServicioTuristicoRequestDTO dto) {
        ServicioTuristicoEntity servicio = new ServicioTuristicoEntity();
        servicio.setNombre(dto.getNombre());
        servicio.setPrecioBase(dto.getPrecioBase());

        return ResponseEntity.ok(servicioTuristicoService.save(servicio));
    }

    /**
     * Actualiza un servicio turístico existente.
     *
     * @param id identificador del servicio a actualizar
     * @param dto nuevos datos del servicio turístico
     * @return entidad actualizada {@link ServicioTuristicoEntity}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServicioTuristicoEntity> update(
            @PathVariable Integer id,
            @RequestBody ServicioTuristicoRequestDTO dto) {

        ServicioTuristicoEntity servicio = servicioTuristicoService.findById(id);
        servicio.setNombre(dto.getNombre());
        servicio.setPrecioBase(dto.getPrecioBase());

        return ResponseEntity.ok(servicioTuristicoService.save(servicio));
    }

    /**
     * Elimina un servicio turístico por su identificador.
     *
     * @param id identificador del servicio a eliminar
     * @return respuesta sin contenido si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        servicioTuristicoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}