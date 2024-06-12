package com.StrattonApp.Backend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.service.EmpleadoService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
                return (UserDetails) empleadoRepository.findByUsername(username)
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

   
    private EmpleadoDTO convertToDTO(Empleado empleado) {
        List<ClienteDTO> clienteDTOs = empleado.getClientes().stream()
                .map(cliente -> new ClienteDTO(cliente.getIdCliente(), cliente.getDNI(), cliente.getNombre(), null, cliente.getApellidos(), null, null, null, null, null, null, null, null, cliente.getFechaSubidaContrato()))
                .collect(Collectors.toList());
        return new EmpleadoDTO(empleado.getNombre(), empleado.getApellidos(), empleado.getEmail(), empleado.getUsername(), empleado.getRoles().toString(), clienteDTOs);
    }

    @Override
    public List<Empleado> getUserById(Long userId) {
        Optional<Empleado> optionalEmpl = empleadoRepository.findById(userId);
        return optionalEmpl.map(List::of).orElse(List.of());
    }

    @Override
    public ClienteDTO getClienteDetallesById(Long clienteId) {
        ClienteDTO cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        return new ClienteDTO(cliente.getIdCliente(), cliente.getCups(), cliente.getCompaniaContratada(), cliente.getFechaSubidaContrato(), cliente.getNombre(), cliente.getApellidos(), cliente.getDNI());
    }
}
