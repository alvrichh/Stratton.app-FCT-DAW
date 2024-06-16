package com.StrattonApp.Backend.controller;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.entities.Suministro;
import com.StrattonApp.Backend.mappers.SuministroMapper;
import com.StrattonApp.Backend.service.SuministroService;

@RestController
@RequestMapping("/api/suministros")
public class SuministroController {
    @Autowired
    private SuministroService suministroService;

    @PostMapping
    public SuministroDTO createSuministro(@RequestBody SuministroDTO suministroDTO) {
        Suministro suministro = SuministroMapper.toEntity(suministroDTO);
        Suministro createdSuministro = suministroService.createSuministro(suministro);
        return SuministroMapper.toDTO(createdSuministro);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuministroDTO> getSuministroById(@PathVariable Long id) {
        Optional<Suministro> suministro = suministroService.getSuministroById(id);
        return suministro.map(value -> ResponseEntity.ok(SuministroMapper.toDTO(value)))
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<SuministroDTO> getAllSuministros() {
        return suministroService.getAllSuministros().stream()
                                .map(SuministroMapper::toDTO)
                                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuministroDTO> updateSuministro(@PathVariable Long id, @RequestBody SuministroDTO suministroDTO) {
        Suministro suministroDetails = SuministroMapper.toEntity(suministroDTO);
        Suministro updatedSuministro = suministroService.updateSuministro(id, suministroDetails);
        return ResponseEntity.ok(SuministroMapper.toDTO(updatedSuministro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuministro(@PathVariable Long id) {
        suministroService.deleteSuministro(id);
        return ResponseEntity.noContent().build();
    }
}
