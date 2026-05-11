package com.example.JourneyMate.service.impl.payment;

import com.example.JourneyMate.dao.payment.MetodoRepository;
import com.example.JourneyMate.entity.payment.MetodoEntity;
import com.example.JourneyMate.service.payment.MetodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de gestión de métodos de pago.
 * Se encarga de las operaciones CRUD sobre los métodos de pago
 * disponibles en el sistema (ej: tarjeta, PayPal, Stripe, etc.).
 */
@Service
public class MetodoServiceImpl implements MetodoService {

    @Autowired
    private MetodoRepository metodoRepository;

    /**
     * Obtiene todos los métodos de pago disponibles.
     */
    @Override
    public List<MetodoEntity> findAll() {
        return metodoRepository.findAll();
    }

    /**
     * Busca un método de pago por su identificador.
     */
    @Override
    public MetodoEntity findById(Integer idMetodo) {
        return metodoRepository.findById(idMetodo).orElse(null);
    }

    /**
     * Guarda o actualiza un método de pago.
     */
    @Override
    public MetodoEntity save(MetodoEntity metodoEntity) {
        return metodoRepository.save(metodoEntity);
    }

    /**
     * Elimina un método de pago por su identificador.
     */
    @Override
    public void deleteById(Integer idMetodo) {
        metodoRepository.deleteById(idMetodo);
    }
}