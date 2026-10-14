package com.StrattonApp.Backend.comparador;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * El comparador es público (sin token) y valida los datos antes de calcular.
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
                .andExpect(jsonPath("$.precioPotenciaP1", closeTo(0.09, 1e-9)));
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
}
