package com.StrattonApp.Backend.comparador;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Comparador público de facturas de luz: el cliente sube su factura, revisa los datos leídos
 * y ve cuánto pagaría con la oferta propia.
 */
@RestController
@RequestMapping("/api/v1/comparador")
@Tag(name = "Comparador")
public class ComparadorController {

    private final ComparadorService comparadorService;
    private final ExtractorFacturaService extractor;
    private final LimitadorPeticiones limitador;

    public ComparadorController(ComparadorService comparadorService, ExtractorFacturaService extractor,
            LimitadorPeticiones limitador) {
        this.comparadorService = comparadorService;
        this.extractor = extractor;
        this.limitador = limitador;
    }

    @Operation(summary = "Precios de la oferta propia y si la lectura automática con IA está disponible")
    @GetMapping("/oferta")
    public Map<String, Object> oferta() {
        ComparadorProperties.Oferta o = comparadorService.oferta();
        return Map.of(
                "nombre", o.nombre(),
                "precioEnergia", o.precioEnergia(),
                "precioPotenciaP1", o.precioPotenciaP1(),
                "precioPotenciaP2", o.precioPotenciaP2(),
                "lecturaAutomatica", extractor.disponible());
    }

    @Operation(summary = "Lee una factura (PDF o imagen) con IA y devuelve sus datos de consumo y precios")
    @PostMapping(value = "/extraer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DatosFactura extraer(@RequestParam("factura") MultipartFile factura, HttpServletRequest req) throws IOException {
        if (factura.isEmpty()) {
            throw new IllegalArgumentException("Selecciona el archivo de la factura");
        }
        limitador.comprobar(req);
        return extractor.extraer(factura.getBytes());
    }

    @Operation(summary = "Compara la factura con la oferta propia aplicando los impuestos vigentes")
    @PostMapping("/calcular")
    public ResultadoComparativa calcular(@Valid @RequestBody DatosFactura datos) {
        return comparadorService.comparar(datos);
    }
}
