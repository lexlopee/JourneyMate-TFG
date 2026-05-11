package com.example.JourneyMate.service.impl.interest;

import com.example.JourneyMate.dao.interest.CategoriaRepository;
import com.example.JourneyMate.entity.interest.CategoriaEntity;
import com.example.JourneyMate.service.interest.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de gestión de categorías de interés.
 * Se encarga de la lógica CRUD para las categorías utilizadas en el sistema
 * de intereses del usuario (ej: playa, montaña, cultura, etc.).
 */
@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    /**
     * Constructor con inyección de dependencias del repositorio de categorías.
     */
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Obtiene todas las categorías registradas en el sistema.
     */
    @Override
    public List<CategoriaEntity> findAll() {
        return categoriaRepository.findAll();
    }

    /**
     * Busca una categoría por su identificador.
     */
    @Override
    public CategoriaEntity findById(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria).orElse(null);
    }

    /**
     * Guarda o actualiza una categoría en la base de datos.
     */
    @Override
    public CategoriaEntity save(CategoriaEntity categoria) {
        return categoriaRepository.save(categoria);
    }

    /**
     * Elimina una categoría por su identificador.
     */
    @Override
    public void deleteById(Integer idCategoria) {
        categoriaRepository.deleteById(idCategoria);
    }
}