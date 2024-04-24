package com.StrattonApp.Backend.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Persona;
import com.StrattonApp.Backend.service.ServicioEmpleado;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

	@Autowired
	private ServicioEmpleado empleadoServicio;
	
	@GetMapping
	public String listarEmpleados(Model model) {
		model.addAttribute("empleados", empleadoServicio.listarTodosLosEmpleados());
		return "empleados";
	}
	
	@GetMapping("/crear")
	public String mostrarFormularioCrear(Model modelo) {
		Empleado empleado = new Empleado();
		empleado.setPersona(new Persona());
		modelo.addAttribute("empleado", empleado);
		return "formulario";
		
	}
	
	@PostMapping("/guardar")
	public String guardarEmpleado(@Valid @ModelAttribute("empleado") Empleado empleado,  BindingResult result, Model model) {
		if (result.hasFieldErrors()) {
			return "formulario";
		}
		empleadoServicio.guardarEmpleado(empleado);
		return "redirect:/empleados";
	}
	@GetMapping("/eliminar/{id}")
	public String eliminarCuenta(@PathVariable Long id) {
		empleadoServicio.eliminarEmpleado(id);
		return "redirect:/empleados";

	}
	@GetMapping("/{id}")
	public String editarCuenta(@PathVariable Long id, Model model) {
		Empleado empleado = empleadoServicio.obtenerEmpleadoPorId(id);
		model.addAttribute("empleado", empleado);
		return "formulario";
		
	}
	
}