package com.StrattonApp.Backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.repository.EmpleadoRepositorio;

@Service
public class ServicioEmpleado {

	@Autowired
	private EmpleadoRepositorio empleadoRepositorio;

	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public ServicioEmpleado(EmpleadoRepositorio empleadoRepositorio) {
		this.empleadoRepositorio = empleadoRepositorio;
	}
	public Iterable<Empleado> listarTodosLosEmpleados(){
		return empleadoRepositorio.findAll();
	}
	public Empleado guardarEmpleado(Empleado empleado) {
		empleado.setPass(passwordEncoder.encode(empleado.getPass()));

		// Establecer la relación bidireccional
		if (empleado.getPersona() != null) {
			empleado.getPersona().setPersona(empleado);
		}
		return empleadoRepositorio.save(empleado);
	}
	public Empleado obtenerEmpleadoPorId(Long id) {
		return empleadoRepositorio.findById(id).orElseThrow(() ->
		new IllegalArgumentException("Empleado no encontrado con id: "+ id));
	}
	public void eliminarEmpleado(Long id) {
		empleadoRepositorio.deleteById(id);
	}

}