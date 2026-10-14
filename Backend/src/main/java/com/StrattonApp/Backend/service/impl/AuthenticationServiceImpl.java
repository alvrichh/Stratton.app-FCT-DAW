package com.StrattonApp.Backend.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.StrattonApp.Backend.DTO.request.SignUpRequest;
import com.StrattonApp.Backend.DTO.request.SigninRequest;
import com.StrattonApp.Backend.DTO.response.JwtAuthenticationResponse;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Role;
import com.StrattonApp.Backend.exceptions.ConflictException;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.service.AuthenticationService;
import com.StrattonApp.Backend.service.JwtService;

/**
 * Implementación de {@link AuthenticationService}.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(EmpleadoRepository empleadoRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService, AuthenticationManager authenticationManager) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        if (empleadoRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new ConflictException("El nombre de usuario ya está en uso");
        }
        if (empleadoRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ConflictException("El correo electrónico ya está en uso");
        }

        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre().trim());
        empleado.setApellidos(request.getApellidos().trim());
        empleado.setUsername(request.getUsername().trim());
        empleado.setEmail(request.getEmail().trim().toLowerCase());
        empleado.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        empleado.setRoles(roles);
        empleado = empleadoRepository.save(empleado);

        return buildResponse(empleado);
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        // Lanza BadCredentialsException (401, ver GlobalExceptionHandler) si las credenciales no son válidas
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Empleado empleado = empleadoRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return buildResponse(empleado);
    }

    private JwtAuthenticationResponse buildResponse(Empleado empleado) {
        String jwt = jwtService.generateToken(empleado);
        return new JwtAuthenticationResponse(jwt, empleado.getMainRole(),
                empleado.getRoles().stream().map(Role::name).sorted().collect(Collectors.toList()),
                empleado.getId(), empleado.getUsername(), empleado.getNombreCompleto());
    }
}
