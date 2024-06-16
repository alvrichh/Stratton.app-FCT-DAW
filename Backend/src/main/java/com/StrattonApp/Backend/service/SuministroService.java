package com.StrattonApp.Backend.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.entities.Suministro;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.SuministroRepository;

@Service
public class SuministroService {
    @Autowired
    private SuministroRepository suministroRepository;

    public Suministro createSuministro(Suministro suministro) {
        return suministroRepository.save(suministro);
    }

    public Optional<Suministro> getSuministroById(Long id) {
        return suministroRepository.findById(id);
    }

    public List<Suministro> getAllSuministros() {
        return suministroRepository.findAll();
    }

    public Suministro updateSuministro(Long id, Suministro suministroDetails) {
        Suministro suministro = suministroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suministro not found"));
        suministro.setNombre(suministroDetails.getNombre());
        suministro.setPrecio(suministroDetails.getPrecio());
        return suministroRepository.save(suministro);
    }

    public void deleteSuministro(Long id) {
        Suministro suministro = suministroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suministro not found"));
        suministroRepository.delete(suministro);
    }
}
