package com.StrattonApp.Backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Comprobación de estado pública y sin acceso a la base de datos. El frontend la consulta al
 * arrancar para saber si el backend está despierto (en el plan gratuito de Render se duerme).
 */
@RestController
@RequestMapping("/api/v1/salud")
@Tag(name = "Salud")
public class SaludController {

    @Operation(summary = "Indica que el backend está en marcha")
    @GetMapping
    public Map<String, String> salud() {
        return Map.of("estado", "ok");
    }
}
