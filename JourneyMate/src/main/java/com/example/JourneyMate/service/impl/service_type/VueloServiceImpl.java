package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.VueloRepository;
import com.example.JourneyMate.entity.service_type.VueloEntity;
import com.example.JourneyMate.service.service_type.VueloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de vuelos.
 * Proporciona operaciones CRUD sobre {@link VueloEntity}.
 */
@Service
public class VueloServiceImpl implements VueloService {

    @Autowired
    private VueloRepository vueloRepository;

    /**
     * Obtiene todos los vuelos registrados.
     *
     * @return lista de vuelos
     */
    @Override
    public List<VueloEntity> findAll() {
        return vueloRepository.findAll();
    }

    /**
     * Busca un vuelo por su identificador.
     *
     * @param idVuelo identificador del vuelo
     * @return la entidad encontrada o null si no existe
     */
    @Override
    public VueloEntity findById(Integer idVuelo) {
        return vueloRepository.findById(idVuelo).orElse(null);
    }

    /**
     * Guarda o actualiza un vuelo.
     *
     * @param vueloEntity entidad a persistir
     * @return la entidad guardada
     */
    @Override
    public VueloEntity save(VueloEntity vueloEntity) {
        return vueloRepository.save(vueloEntity);
    }

    /**
     * Elimina un vuelo por su identificador.
     *
     * @param idVuelo identificador del vuelo a eliminar
     */
    @Override
    public void deleteById(Integer idVuelo) {
        vueloRepository.deleteById(idVuelo);
    }
}