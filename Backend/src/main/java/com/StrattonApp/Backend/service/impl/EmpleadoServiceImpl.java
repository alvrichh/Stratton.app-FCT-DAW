package com.StrattonApp.Backend.service.impl;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.service.EmpleadoService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

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
                .map(empleado -> new EmpleadoDTO(empleado.getFirstName(), empleado.getLastName(), empleado.getEmail(), empleado.getUsername(), empleado.getRoles().toString()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un empleado por su ID.
     *
     * @param userId ID del empleado a obtener.
     * @return Lista que contiene el empleado si se encuentra, o una lista vacía si no.
     */
	@Override
	public List<Empleado> getUserById(Long userid) {
        Optional<Empleado> optionalEmpl = empleadoRepository.findById(userid);

        // Verifica si el usuario existe y retorna una lista con ese usuario o una lista vacía si no se encuentra
        return optionalEmpl.map(List::of).orElse(List.of());
    }
	
}
