package com.StrattonApp.Backend.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.DTO.response.JwtAuthenticationResponse;
import com.StrattonApp.Backend.DTO.resquest.SignUpRequest;
import com.StrattonApp.Backend.DTO.resquest.SigninRequest;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Role;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.service.AuthenticationService;
import com.StrattonApp.Backend.service.JwtService;

import lombok.Builder;

/**
 * Implementación del servicio AuthenticationService para manejar operaciones de autenticación.
 */
@Builder
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private EmpleadoRepository userRepository; // Asegúrate de que UserRepository esté inyectado correctamente
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Constructor para inyección de dependencias
    public AuthenticationServiceImpl(EmpleadoRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     JwtService jwtService,
                                     AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Detalles de registro del usuario.
     * @return Respuesta de autenticación con el token JWT.
     * @throws IllegalArgumentException Si el correo electrónico ya está en uso.
     */
    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        // Corrige la forma de construir el objeto 'User'
        Empleado user = new Empleado();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(Role.USER); // Asegúrate de que Role.USER esté definido correctamente
        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    /**
     * Inicia sesión de un usuario existente en el sistema.
     *
     * @param request Detalles de inicio de sesión del usuario.
     * @return Respuesta de autenticación con el token JWT.
     * @throws IllegalArgumentException Si el nombre de usuario o la contraseña son inválidos.
     */
    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        Empleado user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        
        // Obtener el rol del usuario
        String role = user.getRoles().iterator().next().name();

        String jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).role(role).build();
    }
}