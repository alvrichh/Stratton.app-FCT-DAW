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

/**
 * Implementación de la interfaz{@link EmpleadoService} para manejar operaciones relacionadas con los empleados.
 */
@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Retorna un UserDetailsService que permite cargar los detalles del usuario por nombre de usuario.
     *
     * @return UserDetailsService para cargar detalles del usuario
     */
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return empleadoRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    /**
     * Obtiene todos los empleados y los convierte en una lista de EmpleadoDTO.
     *
     * @return Lista de todos los empleados convertidos a EmpleadoDTO
     */
    @Override
    public List<EmpleadoDTO> getAllUsers() {
        return empleadoRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Obtiene los detalles de un empleado por su ID.
     *
     * @param userId ID del empleado a buscar
     * @return Detalles del empleado encontrado convertidos a EmpleadoDTO
     * @throws ResourceNotFoundException Si no se encuentra el empleado con el ID especificado
     */
    @Override
    public EmpleadoDTO getUserById(Long userId) {
        Empleado empleado = empleadoRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el id: " + userId));
        return convertToDTO(empleado);
    }

    /**
     * Obtiene los detalles de un cliente por su ID.
     *
     * @param clienteId ID del cliente a buscar
     * @return Detalles del cliente encontrado convertidos a ClienteDTO
     * @throws IllegalArgumentException Si no se encuentra el cliente con el ID especificado
     */
    @Override
    public ClienteDTO getClienteDetallesById(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        return new ClienteDTO(
                cliente.getIdCliente(),
                cliente.getCups(),
                cliente.getCompaniaContratada(),
                cliente.getFechaSubidaContrato(),
                cliente.getNombre(),
                cliente.getApellidos(),
                cliente.getDni(),
                cliente.getEmail());
    }

    /**
     * Convierte un objeto Empleado a EmpleadoDTO, incluyendo los detalles de los clientes asociados.
     *
     * @param empleado Empleado a convertir
     * @return EmpleadoDTO generado a partir del empleado dado
     */
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
                                cliente.getDni(),
                                cliente.getEmail());
                    } else {
                        // Manejo cuando la comercializadora es null
                        return new ClienteDTO(
                                cliente.getIdCliente(),
                                cliente.getCups(),
                                cliente.getCompaniaContratada(),
                                cliente.getFechaSubidaContrato(),
                                cliente.getNombre(),
                                cliente.getApellidos(),
                                cliente.getDni(),
                                cliente.getEmail());
                    }
                })
                .collect(Collectors.toList());

        return new EmpleadoDTO(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellidos(),
                empleado.getEmail(),
                empleado.getUsername(),
                empleado.getMainRole(), // getRoles().toString() SIEMPRE ME VA A COGER USER, NO ENTIENDO DONDE ESTÁ EL ERROR
                clienteDTOs);
    }

    @Override
    public Cliente agregarClienteAEmpleado(Long id, ClienteDTO clienteDTO) {
        // Paso 1: Obtener el empleado por su ID
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + id));

        // Paso 2: Convertir ClienteDTO a Cliente
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellidos(clienteDTO.getApellidos());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setIban(cliente.getIban());
        cliente.setComercializadora(cliente.getComercializadora());
        cliente.setDni(cliente.getDni());
        // Asignar el empleado al cliente
        cliente.setEmpleado(empleado);

        // Paso 3: Guardar el cliente
        return clienteRepository.save(cliente);
    }

}
