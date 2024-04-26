package com.StrattonApp.Backend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.service.ServicioEmpleado;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class ServicioEmpleadoImpl implements ServicioEmpleado {

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


	@Override
	public List<Empleado> getUserById(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}
}
