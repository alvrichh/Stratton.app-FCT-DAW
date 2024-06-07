package com.StrattonApp.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.service.ClienteService;
import com.StrattonApp.Backend.service.EmpleadoService;


@RestController
@RequestMapping("/api/v1/empleados")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpleadoController {

	@Autowired
	private EmpleadoRepository empleadoRepositorio;
    // Listar todos los empleados
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Empleado> listarTodosLosEmpleados() {
        return empleadoRepositorio.findAll();
    }

    // Guardar un empleado
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Empleado guardarEmpleado(@RequestBody Empleado empleado) {
        return empleadoRepositorio.save(empleado);
    }

    // Obtener un empleado por usuario
    @GetMapping("/{usuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empleado> obtenerEmpleadoPorUsuario(@PathVariable String usuario) {
        Empleado empleado = empleadoRepositorio.findByUsername(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el usuario: " + usuario));
        return ResponseEntity.ok(empleado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empleado> obtenerEmpleadoPorId(@PathVariable Long id) {
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el usuario: " + id));
        return ResponseEntity.ok(empleado);
    }
    // Actualizar empleado
    @PutMapping("/{usuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empleado> actualizarEmpleado(@PathVariable Long id, @RequestBody Empleado detallesEmpleado) {
        Empleado empleado = empleadoRepositorio.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID : " + id));
        empleado.setNombre(detallesEmpleado.getNombre());
        empleado.setApellidos(detallesEmpleado.getApellidos());
        empleado.setEmail(detallesEmpleado.getEmail());
        
        Empleado empleadoActualizado = empleadoRepositorio.save(empleado);
        return ResponseEntity.ok(empleadoActualizado);

    }

    // Eliminar empleado
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> eliminarEmpleado(@PathVariable Long id) {
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID : " + id));

        empleadoRepositorio.delete(empleado);
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminar", Boolean.TRUE);
        return ResponseEntity.ok(respuesta);
    }
/*
    // Agregar cliente a un empleado
    @PostMapping("/{usuario}/clientes")
    @PreAuthorize("hasRole('ADMIN')")
    public Cliente agregarClienteAEmpleado(@PathVariable String usuario, @RequestBody Cliente cliente) {
        Empleado empleado = empleadoService.obtenerEmpleadoPorUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el usuario: " + usuario));

        cliente.setEmpleado(empleado);
        return clienteService.agregarCliente(cliente);
    }
*/
    /*
    // Listar clientes de un empleado
    @GetMapping("/{id}/clientes")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cliente> listarClientesDeEmpleado(@PathVariable Long id) {
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el usuario: " + id));

        return empleado.getClientes().stream().collect(Collectors.toList());
    }
    @GetMapping("/perfil")
    public ResponseEntity<Empleado> obtenerPerfilEmpleado(Authentication authentication) {
        // Obtener el nombre de usuario del empleado autenticado
        String usuario = authentication.getName();

        // Buscar al empleado por su nombre de usuario
        Empleado empleado = empleadoRepositorio.findByUsername(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el empleado con el usuario: " + usuario));

        // Devolver los datos del empleado encontrado
        return ResponseEntity.ok(empleado);
    }*/
}
