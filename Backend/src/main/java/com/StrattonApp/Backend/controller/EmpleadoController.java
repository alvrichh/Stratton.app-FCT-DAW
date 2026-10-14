package com.StrattonApp.Backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.DTO.request.ClienteRequest;
import com.StrattonApp.Backend.DTO.request.EmpleadoRequest;
import com.StrattonApp.Backend.service.ClienteService;
import com.StrattonApp.Backend.service.EmpleadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Gestión de empleados. Todos los endpoints requieren rol ADMIN salvo {@code /perfil}
 * (ver {@link com.StrattonApp.Backend.config.SecurityConfig}).
 */
@RestController
@RequestMapping("/api/v2/empleados")
@Tag(name = "Empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final ClienteService clienteService;

    public EmpleadoController(EmpleadoService empleadoService, ClienteService clienteService) {
        this.empleadoService = empleadoService;
        this.clienteService = clienteService;
    }

    @Operation(summary = "Lista todos los empleados (ADMIN)")
    @GetMapping
    public List<EmpleadoDTO> listarEmpleados() {
        return empleadoService.getAllEmpleados();
    }

    @Operation(summary = "Perfil del empleado autenticado, con sus clientes")
    @GetMapping("/perfil")
    public EmpleadoDTO perfil() {
        return empleadoService.getPerfil();
    }

    @Operation(summary = "Crea un empleado (ADMIN)")
    @PostMapping
    public ResponseEntity<EmpleadoDTO> crearEmpleado(@Valid @RequestBody EmpleadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.crearEmpleado(request));
    }

    @Operation(summary = "Detalle de un empleado con sus clientes (ADMIN)")
    @GetMapping("/{id}")
    public EmpleadoDTO obtenerEmpleado(@PathVariable Long id) {
        return empleadoService.getEmpleadoById(id);
    }

    @Operation(summary = "Actualiza un empleado (ADMIN). La contraseña solo cambia si se envía.")
    @PutMapping("/{id}")
    public EmpleadoDTO actualizarEmpleado(@PathVariable Long id, @Valid @RequestBody EmpleadoRequest request) {
        return empleadoService.actualizarEmpleado(id, request);
    }

    @Operation(summary = "Elimina un empleado y todos sus clientes (ADMIN)")
    @DeleteMapping("/{id}")
    public Map<String, Boolean> eliminarEmpleado(@PathVariable Long id) {
        empleadoService.eliminarEmpleado(id);
        return Map.of("eliminar", Boolean.TRUE);
    }

    @Operation(summary = "Número de clientes de un empleado (ADMIN)")
    @GetMapping("/{id}/total-clientes")
    public Long totalClientes(@PathVariable Long id) {
        return empleadoService.contarClientes(id);
    }

    @Operation(summary = "Clientes de un empleado (ADMIN)")
    @GetMapping("/{id}/clientes")
    public List<ClienteDTO> clientesDeEmpleado(@PathVariable Long id) {
        return clienteService.getClientesByEmpleadoId(id);
    }

    @Operation(summary = "Crea un cliente asignado a un empleado (ADMIN)")
    @PostMapping("/{id}/clientes")
    public ResponseEntity<ClienteDTO> crearClienteParaEmpleado(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearClienteParaEmpleado(id, request));
    }

    @Operation(summary = "Suministros de todos los clientes de un empleado (ADMIN)")
    @GetMapping("/{id}/suministros-clientes")
    public List<SuministroDTO> suministrosDeClientes(@PathVariable Long id) {
        return empleadoService.getSuministrosDeClientes(id);
    }
}
