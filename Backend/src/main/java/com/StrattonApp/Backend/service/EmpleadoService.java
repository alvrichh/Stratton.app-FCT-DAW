package com.StrattonApp.Backend.service;

import java.util.List;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.DTO.request.EmpleadoRequest;

/**
 * Operaciones de gestión de empleados (ADMIN).
 */
public interface EmpleadoService {

    List<EmpleadoDTO> getAllEmpleados();

    EmpleadoDTO getEmpleadoById(Long id);

    /** Perfil del empleado autenticado, con sus clientes. */
    EmpleadoDTO getPerfil();

    EmpleadoDTO crearEmpleado(EmpleadoRequest request);

    EmpleadoDTO actualizarEmpleado(Long id, EmpleadoRequest request);

    void eliminarEmpleado(Long id);

    long contarClientes(Long empleadoId);

    List<SuministroDTO> getSuministrosDeClientes(Long empleadoId);
}
