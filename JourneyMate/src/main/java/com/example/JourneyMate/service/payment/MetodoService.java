package com.example.JourneyMate.service.payment;

import com.example.JourneyMate.entity.payment.MetodoEntity;

import java.util.List;

/**
 * Servicio para la gestión de métodos de pago.
 * Define las operaciones básicas CRUD sobre {@link MetodoEntity}.
 */
public interface MetodoService {

    /**
     * Obtiene todos los métodos de pago registrados.
     *
     * @return lista de métodos de pago
     */
    List<MetodoEntity> findAll();

    /**
     * Busca un método de pago por su identificador.
     *
     * @param idMetodo identificador del método de pago
     * @return el método de pago encontrado o null si no existe
     */
    MetodoEntity findById(Integer idMetodo);

    /**
     * Guarda o actualiza un método de pago.
     *
     * @param metodoEntity entidad de método de pago
     * @return método de pago persistido
     */
    MetodoEntity save(MetodoEntity metodoEntity);

    /**
     * Elimina un método de pago por su identificador.
     *
     * @param idMetodo identificador del método de pago a eliminar
     */
    void deleteById(Integer idMetodo);
}