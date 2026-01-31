package com.example.JourneyMate.service.impl.interest;

import com.example.JourneyMate.dao.interest.CategoriaRepository;
import com.example.JourneyMate.entity.interest.CategoriaEntity;
import com.example.JourneyMate.service.interest.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<CategoriaEntity> findAll() {
        return categoriaRepository.findAll();
    }

    @Override
    public CategoriaEntity findById(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria).orElse(null);
    }

    @Override
    public CategoriaEntity save(CategoriaEntity categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public void deleteById(Integer idCategoria) {
        categoriaRepository.deleteById(idCategoria);
    }
}
