package com.StrattonApp.Backend.comparador;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * El comparador es público (sin token) y valida los datos antes de calcular.
 * En los tests no hay clave de IA: la lectura automática responde 503 y se usa la entrada manual.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ComparadorApiTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper json;

    private static final DatosFactura FACTURA = new DatosFactura("Endesa", "Tarifa One Luz", 56,
            5.75, 5.75, 335.643, 363.0, 431.0,
            0.102759, 0.014153, 0.194345, 0.194345, 0.194345,
            0.026630, 0.038455, 275.24, 6936.0);

    @Test
    void laOfertaEsPublica() throws Exception {
        mvc.perform(get("/api/v1/comparador/oferta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioEnergia", closeTo(0.10, 1e-9)))
                .andExpect(jsonPath("$.precioPotenciaP1", closeTo(0.09, 1e-9)))
                .andExpect(jsonPath("$.lecturaAutomatica", is(false)));
    }

    @Test
    void calculaSinIniciarSesion() throws Exception {
        mvc.perform(post("/api/v1/comparador/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(FACTURA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oferta.energia", closeTo(112.96, 1e-9)))
                .andExpect(jsonPath("$.ahorro", notNullValue()))
                .andExpect(jsonPath("$.nombreOferta", is("Tarifa Stratton")));
    }

    @Test
    void rechazaDatosIncompletos() throws Exception {
        mvc.perform(post("/api/v1/comparador/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dias\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dias", notNullValue()))
                .andExpect(jsonPath("$.errors.potenciaP1Kw", notNullValue()));
    }

    @Test
    void sinClaveDeIaLaLecturaNoEstaDisponibleYSeLimitaPorIp() throws Exception {
        MockMultipartFile pdf = new MockMultipartFile("factura", "factura.pdf", "application/pdf", "%PDF-1.7".getBytes());
        for (int i = 0; i < 2; i++) {
            mvc.perform(multipart("/api/v1/comparador/extraer").file(pdf).header("X-Forwarded-For", "203.0.113.7"))
                    .andExpect(status().isServiceUnavailable());
        }
        mvc.perform(multipart("/api/v1/comparador/extraer").file(pdf).header("X-Forwarded-For", "203.0.113.7"))
                .andExpect(status().isTooManyRequests());
    }
}
