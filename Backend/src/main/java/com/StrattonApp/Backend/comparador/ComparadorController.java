package com.StrattonApp.Backend.comparador;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Comparador público de facturas de luz. La factura se lee en el navegador del cliente (texto del
 * PDF u OCR): aquí solo llegan sus consumos, potencias y precios para calcular la comparativa.
 */
@RestController
@RequestMapping("/api/v1/comparador")
@Tag(name = "Comparador")
public class ComparadorController {

    private final ComparadorService comparadorService;

    public ComparadorController(ComparadorService comparadorService) {
        this.comparadorService = comparadorService;
    }

    @Operation(summary = "Precios de la oferta propia con la que se compara")
    @GetMapping("/oferta")
    public Map<String, Object> oferta() {
        ComparadorProperties.Oferta o = comparadorService.oferta();
        return Map.of(
                "nombre", o.nombre(),
                "precioEnergia", o.precioEnergia(),
                "precioPotenciaP1", o.precioPotenciaP1(),
                "precioPotenciaP2", o.precioPotenciaP2());
    }

    @Operation(summary = "Compara la factura con la oferta propia aplicando los impuestos vigentes")
    @PostMapping("/calcular")
    public ResultadoComparativa calcular(@Valid @RequestBody DatosFactura datos) {
        return comparadorService.comparar(datos);
    }
}
