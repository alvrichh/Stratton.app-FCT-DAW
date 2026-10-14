package com.StrattonApp.Backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StrattonApp.Backend.DTO.AsesoriaDTO;
import com.StrattonApp.Backend.DTO.ComercializadoraDTO;
import com.StrattonApp.Backend.service.CatalogoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Catálogos de solo lectura que necesita el frontend para rellenar los desplegables.
 */
@RestController
@RequestMapping("/api/v2")
@Tag(name = "Catálogos")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @Operation(summary = "Comercializadoras disponibles")
    @GetMapping("/comercializadoras")
    public List<ComercializadoraDTO> comercializadoras() {
        return catalogoService.getComercializadoras();
    }

    @Operation(summary = "Asesorías disponibles")
    @GetMapping("/asesorias")
    public List<AsesoriaDTO> asesorias() {
        return catalogoService.getAsesorias();
    }
}
