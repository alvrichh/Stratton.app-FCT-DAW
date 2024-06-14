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
import com.StrattonApp.Backend.exceptions.GlobalException;
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

    private EmpleadoRepository userRepository; // Repositorio para acceder a los datos de los empleados
    private final PasswordEncoder passwordEncoder; // Codificador de contraseñas
    private final JwtService jwtService; // Servicio para generación y validación de tokens JWT
    private final AuthenticationManager authenticationManager; // Gestor de autenticación

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
        // Verifica si el correo electrónico ya está en uso
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        // Crea un nuevo objeto Empleado y guarda en la base de datos
        Empleado user = new Empleado();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(Role.USER); // Asigna un rol por defecto al nuevo usuario
        userRepository.save(user);

        // Genera un token JWT para el usuario registrado
        String jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    /**
     * Inicia sesión de un usuario existente en el sistema.
     *
     * @param request Detalles de inicio de sesión del usuario.
     * @return Respuesta de autenticación con el token JWT y el rol del usuario.
     * @throws GlobalException Si el nombre de usuario o la contraseña son inválidos.
     */
    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        try {
            // Autentica al usuario utilizando el AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // Obtiene el usuario autenticado desde la base de datos
            Empleado user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new GlobalException("Usuario no encontrado"));

            // Obtiene el rol del usuario autenticado
            String role = user.getRoles().iterator().next().name();

            // Genera un token JWT para el usuario autenticado
            String jwt = jwtService.generateToken(user);
            return JwtAuthenticationResponse.builder().token(jwt).role(role).build();

        } catch (Exception e) {
            // Captura cualquier excepción de autenticación y la maneja como GlobalException
            throw new GlobalException("Contraseña no válida para usuario: " + request.getUsername());
        }
    }

    // Getter para el AuthenticationManager (útil para configuración)
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }
    
}
