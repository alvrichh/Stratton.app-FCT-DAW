package com.StrattonApp.Backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.DTO.request.SuministroRequest;
import com.StrattonApp.Backend.service.SuministroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Actualización y borrado de suministros por su CUPS.
 */
@RestController
@RequestMapping("/api/v2/suministros")
@Tag(name = "Suministros")
public class SuministroController {

    private final SuministroService suministroService;

    public SuministroController(SuministroService suministroService) {
        this.suministroService = suministroService;
    }

    @Operation(summary = "Actualiza un suministro (estado, dirección, potencia, comercializadora)")
    @PutMapping("/{cups}")
    public SuministroDTO actualizar(@PathVariable String cups, @Valid @RequestBody SuministroRequest request) {
        return suministroService.actualizarSuministro(cups, request);
    }

    @Operation(summary = "Elimina un suministro")
    @DeleteMapping("/{cups}")
    public ResponseEntity<Void> eliminar(@PathVariable String cups) {
        suministroService.eliminarSuministro(cups);
        return ResponseEntity.noContent().build();
    }
}
