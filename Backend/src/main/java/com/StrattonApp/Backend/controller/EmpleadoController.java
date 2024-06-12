package com.StrattonApp.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.service.EmpleadoService;

@RestController
@RequestMapping("/api/v1/empleados")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository empleadoRepositorio;

    @Autowired
    private EmpleadoService empleadoService;

    // Listar todos los empleados
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<EmpleadoDTO> listarTodosLosEmpleados() {
        return empleadoService.getAllUsers();
    }

    // Guardar un empleado
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EmpleadoDTO guardarEmpleado(@RequestBody Empleado empleado) {
        Empleado savedEmpleado = empleadoRepositorio.save(empleado);
        return empleadoService.convertToDTO(savedEmpleado);
    }

    // Obtener un empleado por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorId(@PathVariable Long id) {
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el id: " + id));
        EmpleadoDTO empleadoDTO = empleadoService.convertToDTO(empleado);
        return ResponseEntity.ok(empleadoDTO);
    }

    // Actualizar empleado
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody Empleado detallesEmpleado) {
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + id));

        empleado.setNombre(detallesEmpleado.getNombre());
        empleado.setApellidos(detallesEmpleado.getApellidos());
        empleado.setEmail(detallesEmpleado.getEmail());
        empleado.setUsername(detallesEmpleado.getUsername());
        empleado.setPassword(detallesEmpleado.getPassword());
        empleado.setRoles(detallesEmpleado.getRoles());
        empleado.setAsesoria(detallesEmpleado.getAsesoria());
        empleado.setClientes(detallesEmpleado.getClientes());

        Empleado empleadoActualizado = empleadoRepositorio.save(empleado);
        EmpleadoDTO empleadoDTO = empleadoService.convertToDTO(empleadoActualizado);
        return ResponseEntity.ok(empleadoDTO);
    }

    // Eliminar empleado
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> eliminarEmpleado(@PathVariable Long id) {
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + id));

        empleadoRepositorio.delete(empleado);
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminar", Boolean.TRUE);
        return ResponseEntity.ok(respuesta);
    }

    // Obtener perfil del empleado autenticado
    @GetMapping("/perfil")
    public ResponseEntity<EmpleadoDTO> obtenerPerfilEmpleado(Authentication authentication) {
        String usuario = authentication.getName();
        Empleado empleado = empleadoRepositorio.findByUsername(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el empleado con el usuario: " + usuario));
        EmpleadoDTO empleadoDTO = empleadoService.convertToDTO(empleado);
        return ResponseEntity.ok(empleadoDTO);
    }
    
}
