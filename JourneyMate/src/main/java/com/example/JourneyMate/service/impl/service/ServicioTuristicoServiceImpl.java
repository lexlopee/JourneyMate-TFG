package com.example.JourneyMate.service.impl.service;

import com.example.JourneyMate.dao.service.ServicioTuristicoRepository;
import com.example.JourneyMate.entity.service.ServicioTuristicoEntity;
import com.example.JourneyMate.service.service.ServicioTuristicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de servicios turísticos.
 *
 * Este servicio actúa como capa de acceso a la entidad base {@link ServicioTuristicoEntity}.
 * No gestiona directamente entidades hijas (Hotel, Apartamento, Actividad), ya que
 * la lógica de creación de subtipos se delega a {@code ReservaServiceImpl} utilizando
 * herencia JPA (@Inheritance JOINED).
 */
@Service
@RequiredArgsConstructor
public class ServicioTuristicoServiceImpl implements ServicioTuristicoService {

    private final ServicioTuristicoRepository servicioTuristicoRepository;

    /**
     * Obtiene todos los servicios turísticos registrados.
     *
     * @return lista de servicios turísticos
     */
    @Override
    public List<ServicioTuristicoEntity> findAll() {
        return servicioTuristicoRepository.findAll();
    }

    /**
     * Busca un servicio turístico por su identificador.
     *
     * @param idTuristico identificador del servicio turístico
     * @return la entidad encontrada
     * @throws RuntimeException si el servicio no existe
     */
    @Override
    public ServicioTuristicoEntity findById(Integer idTuristico) {
        return servicioTuristicoRepository.findById(idTuristico)
                .orElseThrow(() -> new RuntimeException("Servicio turístico no encontrado: " + idTuristico));
    }

    /**
     * Guarda un servicio turístico en la base de datos.
     *
     * Nota: este método persiste únicamente en la tabla base {@code servicio_turistico},
     * sin crear entidades hijas.
     *
     * @param servicioTuristico entidad a guardar
     * @return entidad persistida
     */
    @Override
    public ServicioTuristicoEntity save(ServicioTuristicoEntity servicioTuristico) {
        return servicioTuristicoRepository.save(servicioTuristico);
    }

    /**
     * Elimina un servicio turístico por su identificador.
     *
     * @param idTuristico identificador del servicio a eliminar
     */
    @Override
    public void deleteById(Integer idTuristico) {
        servicioTuristicoRepository.deleteById(idTuristico);
    }
}