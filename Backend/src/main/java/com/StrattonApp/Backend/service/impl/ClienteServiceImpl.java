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

/**
 * Implementación de {@link ClienteService} que maneja la lógica de negocio para la gestión de clientes.
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Obtiene todos los clientes y los convierte a {@link ClienteDTO}.
     *
     * @return Lista de todos los clientes convertidos a DTO.
     */
    @Override
    public List<ClienteDTO> getAllClientes() {
        return clienteRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Obtiene un cliente por su ID y lo convierte a {@link ClienteDTO}.
     *
     * @param clienteId ID del cliente a buscar.
     * @return Cliente encontrado convertido a DTO.
     * @throws ResourceNotFoundException Si no se encuentra el cliente con el ID especificado.
     */
    @Override
    public ClienteDTO getClienteById(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID: " + clienteId));
        return convertToDTO(cliente);
    }

    /**
     * Obtiene todos los clientes asociados a un empleado por su ID y los convierte a {@link ClienteDTO}.
     *
     * @param empleadoId ID del empleado para filtrar clientes.
     * @return Lista de clientes asociados al empleado convertidos a DTO.
     */
    @Override
    public List<ClienteDTO> getClientesByEmpleadoId(Long empleadoId) {
        return clienteRepository.findByEmpleadoId(empleadoId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Guarda un nuevo cliente en la base de datos y lo devuelve como {@link ClienteDTO}.
     *
     * @param cliente Cliente a guardar.
     * @return Cliente guardado convertido a DTO.
     */
    @Override
    public ClienteDTO guardarCliente(Cliente cliente) {
        Cliente savedCliente = clienteRepository.save(cliente);
        return convertToDTO(savedCliente);
    }

    /**
     * Actualiza los detalles de un cliente existente por su ID y devuelve el cliente actualizado como {@link ClienteDTO}.
     *
     * @param clienteId       ID del cliente a actualizar.
     * @param detallesCliente Detalles actualizados del cliente.
     * @return Cliente actualizado convertido a DTO.
     * @throws ResourceNotFoundException Si no se encuentra el cliente con el ID especificado.
     */
    @Override
    public ClienteDTO actualizarCliente(Long clienteId, Cliente detallesCliente) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID: " + clienteId));

        // Actualiza los campos del cliente con los detalles proporcionados
        cliente.setNombre(detallesCliente.getNombre());
        cliente.setApellidos(detallesCliente.getApellidos());
        cliente.setDni(detallesCliente.getDni());
        cliente.setEmail(detallesCliente.getEmail());
        cliente.setDireccion(detallesCliente.getDireccion());
        cliente.setTelefono(detallesCliente.getTelefono());
        cliente.setIban(detallesCliente.getIban());

        // Puedes continuar actualizando otros campos según sea necesario

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertToDTO(clienteActualizado);
    }

    /**
     * Elimina un cliente por su ID.
     *
     * @param clienteId ID del cliente a eliminar.
     * @throws ResourceNotFoundException Si no se encuentra el cliente con el ID especificado.
     */
    @Override
    public void eliminarCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID: " + clienteId));
        clienteRepository.delete(cliente);
    }

    /**
     * Método privado para convertir un {@link Cliente} a {@link ClienteDTO}.
     *
     * @param cliente Cliente a convertir.
     * @return Cliente convertido a DTO.
     */
    // Método para convertir Cliente a ClienteDTO
    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setIdCliente(cliente.getIdCliente());
        clienteDTO.setCups(cliente.getCups());
        clienteDTO.setCompaniaContratada(cliente.getCompaniaContratada());
        clienteDTO.setNombre(cliente.getNombre());
        clienteDTO.setApellidos(cliente.getApellidos());
        clienteDTO.setDni(cliente.getDni());
        clienteDTO.setFechaSubidaContrato(cliente.getFechaSubidaContrato());
        clienteDTO.setEmail(cliente.getEmail());
        clienteDTO.setDireccion(cliente.getDireccion()); //nuevo campo
        // Establecer otros campos si es necesario
        return clienteDTO;
    }

}
