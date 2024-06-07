package com.StrattonApp.Backend.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.entities.Empleado;



/**
 * Interfaz que define operaciones relacionadas con usuarios en el sistema.
 */
public interface EmpleadoService {
    
    /**
     * Obtiene un servicio de detalles de usuario para la autenticación.
     *
     * @return Un servicio de detalles de usuario.
     */
    UserDetailsService userDetailsService();
    
    /**
     * Obtiene una lista de todos los usuarios en el sistema.
     *
     * @return Lista de objetos UsuarioDTO que representan a todos los usuarios.
     */
    List<EmpleadoDTO> getAllUsers();
    
    /**
     * Obtiene un usuario por su ID.
     *
     * @param userId El ID del usuario a buscar.
     * @return Lista de usuarios encontrados (puede contener uno o ningún usuario).
     */
    List<Empleado> getUserById(Long userId);


}