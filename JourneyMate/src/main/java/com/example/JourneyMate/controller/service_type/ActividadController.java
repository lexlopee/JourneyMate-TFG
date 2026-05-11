package com.example.JourneyMate.controller.service_type;

import com.example.JourneyMate.entity.service_type.ActividadEntity;
import com.example.JourneyMate.service.service_type.ActividadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de gestionar las operaciones CRUD
 * relacionadas con las actividades turísticas.
 *
 * Expone endpoints para listar, consultar, crear, actualizar y eliminar
 * actividades dentro del sistema.
 */
@RestController
@RequestMapping("/actividades")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;

    /**
     * Obtiene la lista de todas las actividades disponibles.
     *
     * @return lista de {@link ActividadEntity}
     */
    @GetMapping
    public ResponseEntity<List<ActividadEntity>> findAll() {
        return ResponseEntity.ok(actividadService.findAll());
    }

    /**
     * Obtiene una actividad por su identificador.
     *
     * @param id identificador de la actividad
     * @return entidad {@link ActividadEntity} si existe,
     *         o respuesta 404 si no se encuentra
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActividadEntity> findById(@PathVariable Integer id) {
        ActividadEntity actividad = actividadService.findByIdActividad(id);
        return actividad != null
                ? ResponseEntity.ok(actividad)
                : ResponseEntity.notFound().build();
    }

    /**
     * Crea una nueva actividad turística.
     *
     * @param actividad entidad a crear
     * @return actividad creada {@link ActividadEntity}
     */
    @PostMapping
    public ResponseEntity<ActividadEntity> create(@RequestBody ActividadEntity actividad) {
        return ResponseEntity.ok(actividadService.saveActividad(actividad));
    }

    /**
     * Actualiza una actividad existente.
     *
     * Si la actividad no existe, devuelve una respuesta 404.
     *
     * @param id identificador de la actividad a actualizar
     * @param actividad datos actualizados de la actividad
     * @return actividad actualizada {@link ActividadEntity} o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<ActividadEntity> update(@PathVariable Integer id,
                                                  @RequestBody ActividadEntity actividad) {

        if (actividadService.findByIdActividad(id) == null) {
            return ResponseEntity.notFound().build();
        }

        // porque hereda de ServicioTuristicoEntity
        actividad.setIdServicio(id);

        return ResponseEntity.ok(actividadService.saveActividad(actividad));
    }

    /**
     * Elimina una actividad por su identificador.
     *
     * Si la actividad no existe, devuelve 404.
     *
     * @param id identificador de la actividad a eliminar
     * @return respuesta sin contenido si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        if (actividadService.findByIdActividad(id) == null) {
            return ResponseEntity.notFound().build();
        }

        actividadService.deleteByIdActividad(id);
        return ResponseEntity.noContent().build();
    }
}