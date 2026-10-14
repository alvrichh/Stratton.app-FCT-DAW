package com.StrattonApp.Backend.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.DTO.request.EmpleadoRequest;
import com.StrattonApp.Backend.entities.Asesoria;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Role;
import com.StrattonApp.Backend.exceptions.ConflictException;
import com.StrattonApp.Backend.exceptions.GlobalException;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.AsesoriaRepository;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.repository.SuministroRepository;
import com.StrattonApp.Backend.security.SecurityUtils;
import com.StrattonApp.Backend.service.EmpleadoService;

/**
 * Implementación de {@link EmpleadoService}.
 */
@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final ClienteRepository clienteRepository;
    private final SuministroRepository suministroRepository;
    private final AsesoriaRepository asesoriaRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, ClienteRepository clienteRepository,
            SuministroRepository suministroRepository, AsesoriaRepository asesoriaRepository,
            PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.clienteRepository = clienteRepository;
        this.suministroRepository = suministroRepository;
        this.asesoriaRepository = asesoriaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> getAllEmpleados() {
        return empleadoRepository.findAllByOrderByApellidosAscNombreAsc().stream()
                .map(e -> EmpleadoDTO.from(e, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoDTO getEmpleadoById(Long id) {
        return EmpleadoDTO.from(findEmpleado(id), true);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoDTO getPerfil() {
        Empleado actual = SecurityUtils.currentEmpleado();
        return EmpleadoDTO.from(findEmpleado(actual.getId()), true);
    }

    @Override
    public EmpleadoDTO crearEmpleado(EmpleadoRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new GlobalException("La contraseña es obligatoria al crear un empleado");
        }
        if (empleadoRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new ConflictException("El nombre de usuario ya está en uso");
        }
        if (empleadoRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ConflictException("El correo electrónico ya está en uso");
        }
        Empleado empleado = new Empleado();
        aplicar(empleado, request);
        empleado.setPassword(passwordEncoder.encode(request.getPassword()));
        return EmpleadoDTO.from(empleadoRepository.save(empleado), false);
    }

    @Override
    public EmpleadoDTO actualizarEmpleado(Long id, EmpleadoRequest request) {
        Empleado empleado = findEmpleado(id);

        empleadoRepository.findByUsernameIgnoreCase(request.getUsername())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> { throw new ConflictException("El nombre de usuario ya está en uso"); });
        empleadoRepository.findByEmailIgnoreCase(request.getEmail())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> { throw new ConflictException("El correo electrónico ya está en uso"); });

        // Un administrador no puede quitarse a sí mismo el rol ADMIN
        Empleado actual = SecurityUtils.currentEmpleado();
        if (actual.getId().equals(id) && request.getRoles() != null && !request.getRoles().contains(Role.ADMIN)) {
            throw new GlobalException("No puedes quitarte a ti mismo el rol de administrador");
        }

        aplicar(empleado, request);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            empleado.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return EmpleadoDTO.from(empleadoRepository.save(empleado), true);
    }

    @Override
    public void eliminarEmpleado(Long id) {
        Empleado empleado = findEmpleado(id);
        Empleado actual = SecurityUtils.currentEmpleado();
        if (actual.getId().equals(id)) {
            throw new GlobalException("No puedes eliminar tu propio usuario");
        }
        // Los clientes del empleado se eliminan en cascada (orphanRemoval), y con ellos sus suministros.
        empleadoRepository.delete(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarClientes(Long empleadoId) {
        return clienteRepository.countByEmpleado(findEmpleado(empleadoId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuministroDTO> getSuministrosDeClientes(Long empleadoId) {
        findEmpleado(empleadoId);
        return suministroRepository.findByClienteEmpleadoIdOrderByCupsAsc(empleadoId).stream()
                .map(SuministroDTO::from)
                .collect(Collectors.toList());
    }

    // ----- helpers -----------------------------------------------------------

    private Empleado findEmpleado(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + id));
    }

    private void aplicar(Empleado empleado, EmpleadoRequest request) {
        empleado.setNombre(request.getNombre().trim());
        empleado.setApellidos(request.getApellidos().trim());
        empleado.setUsername(request.getUsername().trim());
        empleado.setEmail(request.getEmail().trim().toLowerCase());
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null) {
            roles.addAll(request.getRoles());
        }
        if (roles.isEmpty()) {
            roles.add(Role.USER);
        }
        empleado.setRoles(roles);
        if (request.getAsesoriaId() != null) {
            Asesoria asesoria = asesoriaRepository.findById(request.getAsesoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la asesoría con el ID: " + request.getAsesoriaId()));
            empleado.setAsesoria(asesoria);
        }
    }
}
