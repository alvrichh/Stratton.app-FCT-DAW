package com.StrattonApp.Backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.StrattonApp.Backend.DTO.AsesoriaDTO;
import com.StrattonApp.Backend.DTO.ComercializadoraDTO;
import com.StrattonApp.Backend.repository.AsesoriaRepository;
import com.StrattonApp.Backend.repository.ComercializadoraRepository;

/**
 * Catálogos de solo lectura (comercializadoras y asesorías).
 */
@Service
@Transactional(readOnly = true)
public class CatalogoService {

    private final ComercializadoraRepository comercializadoraRepository;
    private final AsesoriaRepository asesoriaRepository;

    public CatalogoService(ComercializadoraRepository comercializadoraRepository, AsesoriaRepository asesoriaRepository) {
        this.comercializadoraRepository = comercializadoraRepository;
        this.asesoriaRepository = asesoriaRepository;
    }

    public List<ComercializadoraDTO> getComercializadoras() {
        return comercializadoraRepository.findAllByOrderByCompaniaContratadaAsc().stream()
                .map(ComercializadoraDTO::from).toList();
    }

    public List<AsesoriaDTO> getAsesorias() {
        return asesoriaRepository.findAllByOrderByNombreAsc().stream().map(AsesoriaDTO::from).toList();
    }
}
