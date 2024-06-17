package com.StrattonApp.Backend.service;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;

import java.util.List;

public interface ClienteService {

    // Obtener todos los clientes y mapearlos a DTOs
    List<ClienteDTO> getAllClientes();

    // Obtener un cliente por su ID y mapearlo a un DTO
    ClienteDTO getClienteById(Long clienteId);

    // Obtener clientes por el ID del empleado asociado y mapearlos a DTOs
    List<ClienteDTO> getClientesByEmpleadoId(Long empleadoId);

    // Guardar un nuevo cliente y devolver su DTO correspondiente
    ClienteDTO guardarCliente(Cliente cliente);

    // Actualizar los detalles de un cliente existente y devolver su DTO actualizado
    ClienteDTO actualizarCliente(Long clienteId, Cliente detallesCliente);

    // Eliminar un cliente por su ID
    void eliminarCliente(Long clienteId);

	ClienteDTO guardarClientePorEmpleado(Long empleadoId, Cliente cliente);

}
