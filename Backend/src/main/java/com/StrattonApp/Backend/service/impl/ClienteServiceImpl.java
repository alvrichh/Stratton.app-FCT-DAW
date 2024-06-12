package com.StrattonApp.Backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.service.ClienteService;


import java.util.List;
import java.util.stream.Collectors;
@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository; 

    private ClienteDTO mapToDTO(Cliente cliente) {
        return new ClienteDTO(
            cliente.getIdCliente(),
            cliente.getSuministros(),
            cliente.getComercializadora(),
            cliente.getFechaSubidaContrato(),
            cliente.getNombre(),
            cliente.getApellidos(),
            cliente.getDNI(),
            cliente.getEmail(),
            cliente.getDireccion(),
            cliente.getTelefono(),
            cliente.getIBAN(),
            cliente.getAsesoria() != null ? cliente.getAsesoria().getIdAsesoria() : null,
            cliente.getComercializadora() != null ? cliente.getComercializadora().getIdComercializadora() : null,
            cliente.getEmpleado() != null ? cliente.getEmpleado().getIdEmpleado() : null
        );
    }
    
    @Override
    public List<ClienteDTO> obtenerClientesDTO() {
        List<Cliente> clientes = clienteRepository.findAll();
        // Convertir objetos Cliente en objetos ClienteDTO
        return clientes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClienteDTO getClienteDetallesById(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        return mapToDTO(cliente);
    }
}
