package com.example.JourneyMate.service.interest;

import com.example.JourneyMate.entity.interest.CategoriaEntity;

import java.util.List;

/**
 * Servicio para la gestión de categorías de interés.
 * Define las operaciones básicas CRUD sobre {@link CategoriaEntity}.
 */
public interface CategoriaService {

    /**
     * Obtiene todas las categorías registradas.
     *
     * @return lista de categorías
     */
    List<CategoriaEntity> findAll();

    /**
     * Busca una categoría por su identificador.
     *
     * @param idCategoria identificador de la categoría
     * @return la categoría encontrada o null si no existe
     */
    CategoriaEntity findById(Integer idCategoria);

    /**
     * Guarda o actualiza una categoría.
     *
     * @param categoria entidad de categoría
     * @return categoría persistida
     */
    CategoriaEntity save(CategoriaEntity categoria);

    /**
     * Elimina una categoría por su identificador.
     *
     * @param idCategoria identificador de la categoría a eliminar
     */
    void deleteById(Integer idCategoria);
}