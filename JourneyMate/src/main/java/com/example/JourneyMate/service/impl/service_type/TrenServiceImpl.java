package com.example.JourneyMate.service.impl.service_type;

import com.example.JourneyMate.dao.service_type.TrenRepository;
import com.example.JourneyMate.entity.service_type.TrenEntity;
import com.example.JourneyMate.service.service_type.TrenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrenServiceImpl implements TrenService {

   @Autowired
   private TrenRepository trenRepository;

    @Override
    public List<TrenEntity> findAll() {
        return trenRepository.findAll();
    }

    @Override
    public TrenEntity findById(Integer idTren) {
        return trenRepository.findById(idTren).orElse(null);
    }

    @Override
    public TrenEntity save(TrenEntity trenEntity) {
        return trenRepository.save(trenEntity);
    }

    @Override
    public void deleteById(Integer idTren) {
        trenRepository.deleteById(idTren);
    }
}
