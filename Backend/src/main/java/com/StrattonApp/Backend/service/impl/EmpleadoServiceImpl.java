package com.StrattonApp.Backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.service.EmpleadoService;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return empleadoRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    @Override
    public List<EmpleadoDTO> getAllUsers() {
        return empleadoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    

    @Override
    public EmpleadoDTO getUserById(Long userId) {
        Empleado empleado = empleadoRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el id: " + userId));
        return convertToDTO(empleado);
    }

    @Override
    public ClienteDTO getClienteDetallesById(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        return new ClienteDTO(
                cliente.getIdCliente(),
                cliente.getCups(),
                cliente.getCompaniaContratada(),
                cliente.getNombre(),
                cliente.getApellidos(),
                cliente.getDNI(),
                cliente.getFechaSubidaContrato());
    }


    @Override
    public EmpleadoDTO convertToDTO(Empleado empleado) {
        List<ClienteDTO> clienteDTOs = empleado.getClientes().stream()
                .map(cliente -> {
                    if (cliente.getComercializadora() != null) {
                        return new ClienteDTO(
                                cliente.getIdCliente(),
                                cliente.getCups(),
                                cliente.getComercializadora().getNombre(),
                                cliente.getFechaSubidaContrato(),
                                cliente.getNombre(),
                                cliente.getApellidos(),
                                cliente.getDNI());
                    } else {
                        // Manejo cuando la comercializadora es null
                        return new ClienteDTO(
                                cliente.getIdCliente(),
                                cliente.getCups(),
                                null, // Puedes manejar esto según tus necesidades
                                cliente.getFechaSubidaContrato(),
                                cliente.getNombre(),
                                cliente.getApellidos(),
                                cliente.getDNI());
                    }
                })
                .collect(Collectors.toList());

        return new EmpleadoDTO(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellidos(),
                empleado.getEmail(),
                empleado.getUsername(),
                empleado.getRoles().toString(),
                clienteDTOs);
    }
}
