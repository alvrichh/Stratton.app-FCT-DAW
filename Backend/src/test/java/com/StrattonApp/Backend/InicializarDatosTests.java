package com.StrattonApp.Backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.StrattonApp.Backend.config.InicializarDatos;
import com.StrattonApp.Backend.repository.AsesoriaRepository;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.repository.SuministroRepository;

/**
 * SEED_RESET=true sobre una base de datos ya poblada debe vaciarla respetando las claves foráneas
 * y volver a sembrar los datos de ejemplo.
 */
@SpringBootTest
@DirtiesContext
class InicializarDatosTests {

    @Autowired
    private InicializarDatos inicializarDatos;
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private SuministroRepository suministroRepository;
    @Autowired
    private AsesoriaRepository asesoriaRepository;

    @Test
    void resetSobreBaseDeDatosPobladaVuelveASembrar() {
        assertThat(empleadoRepository.count()).isEqualTo(3);
        long clientesAntes = clienteRepository.count();

        inicializarDatos.setSeedReset(true);
        inicializarDatos.run();

        assertThat(empleadoRepository.count()).isEqualTo(3);
        assertThat(asesoriaRepository.count()).isEqualTo(2);
        assertThat(clienteRepository.count()).isEqualTo(clientesAntes);
        assertThat(suministroRepository.count()).isGreaterThan(10);
        assertThat(empleadoRepository.findByUsernameIgnoreCase("admin")).isPresent();
    }
}
