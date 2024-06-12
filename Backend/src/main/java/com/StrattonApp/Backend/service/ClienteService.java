package com.StrattonApp.Backend.service;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;

import java.util.List;

public interface ClienteService {

	List<ClienteDTO> getAllClientes();

	ClienteDTO getClienteById(Long clienteId);

	List<ClienteDTO> getClientesByEmpleadoId(Long empleadoId);

	ClienteDTO guardarCliente(Cliente cliente);

	ClienteDTO actualizarCliente(Long clienteId, Cliente detallesCliente);

	void eliminarCliente(Long clienteId);

}
