package com.StrattonApp.Backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public List<ClienteDTO> getAllClientes() {
        return clienteRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public ClienteDTO getClienteById(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID: " + clienteId));
        return convertToDTO(cliente);
    }

    @Override
    public List<ClienteDTO> getClientesByEmpleadoId(Long empleadoId) {
        // Implementa la lógica para obtener clientes por ID de empleado
        return null;
    }

    @Override
    public ClienteDTO guardarCliente(Cliente cliente) {
        Cliente savedCliente = clienteRepository.save(cliente);
        return convertToDTO(savedCliente);
    }

    @Override
    public ClienteDTO actualizarCliente(Long clienteId, Cliente detallesCliente) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID: " + clienteId));

        // Actualiza los campos del cliente con los detalles proporcionados
        cliente.setNombre(detallesCliente.getNombre());
        cliente.setApellidos(detallesCliente.getApellidos());
        // Actualiza los demás campos según sea necesario

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertToDTO(clienteActualizado);
    }

    @Override
    public void eliminarCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID: " + clienteId));
        clienteRepository.delete(cliente);
    }

    // Método para convertir Cliente a ClienteDTO
    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(cliente.getIdCliente());
        clienteDTO.setCups(cliente.getCups());
        clienteDTO.setCompaniaContratada(cliente.getCompaniaContratada());
        clienteDTO.setNombre(cliente.getNombre());
        clienteDTO.setApellidos(cliente.getApellidos());
        clienteDTO.setDni(cliente.getDNI());
        clienteDTO.setFechaSubidaContrato(cliente.getFechaSubidaContrato());
        clienteDTO.setEmail(cliente.getEmail());
        // Establece otros campos si es necesario
        return clienteDTO;
    }

}
