package com.StrattonApp.Backend.service;

import java.util.List;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.DTO.request.ClienteRequest;

/**
 * Gestión de clientes. Un USER solo ve y modifica sus propios clientes;
 * ADMIN y MANAGER ven los de todos los empleados.
 */
public interface ClienteService {

    /** Clientes visibles para el usuario autenticado. */
    List<ClienteDTO> getClientesVisibles();

    List<ClienteDTO> getClientesByEmpleadoId(Long empleadoId);

    ClienteDTO getClienteById(Long clienteId);

    /** Crea un cliente asignado al empleado autenticado (o al indicado en la petición si es jefe). */
    ClienteDTO crearCliente(ClienteRequest request);

    /** Crea un cliente asignado al empleado indicado. */
    ClienteDTO crearClienteParaEmpleado(Long empleadoId, ClienteRequest request);

    ClienteDTO actualizarCliente(Long clienteId, ClienteRequest request);

    void eliminarCliente(Long clienteId);
}
