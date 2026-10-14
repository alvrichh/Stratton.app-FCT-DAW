package com.StrattonApp.Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.StrattonApp.Backend.repository.EmpleadoRepository;

/**
 * Beans básicos de autenticación, separados de {@link SecurityConfig} para evitar
 * dependencias circulares (el filtro JWT necesita el UserDetailsService).
 */
@Configuration
public class ApplicationConfig {

    private final EmpleadoRepository empleadoRepository;

    public ApplicationConfig(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    /** Carga los empleados por nombre de usuario (sin distinguir mayúsculas). */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> empleadoRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
