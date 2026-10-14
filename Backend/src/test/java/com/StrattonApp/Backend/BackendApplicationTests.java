package com.StrattonApp.Backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.repository.SuministroRepository;

/**
 * El contexto arranca con H2 (ver src/test/resources/application.properties) y se cargan los datos de ejemplo.
 */
@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private SuministroRepository suministroRepository;

    @Test
    void contextLoadsAndSeedsSampleData() {
        assertThat(empleadoRepository.findByUsernameIgnoreCase("admin")).isPresent();
        assertThat(empleadoRepository.findByUsernameIgnoreCase("usuario")).isPresent();
        assertThat(clienteRepository.count()).isGreaterThanOrEqualTo(10);
        assertThat(suministroRepository.count()).isGreaterThan(10);
    }
}
