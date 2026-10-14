package com.StrattonApp.Backend.controller;

import java.util.List;

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
import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.DTO.request.ClienteRequest;
import com.StrattonApp.Backend.DTO.request.SuministroRequest;
import com.StrattonApp.Backend.service.ClienteService;
import com.StrattonApp.Backend.service.SuministroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * CRUD de clientes y de sus suministros. Un USER solo accede a sus propios clientes;
 * ADMIN y MANAGER a los de todos.
 */
@RestController
@RequestMapping("/api/v2/clientes")
@Tag(name = "Clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final SuministroService suministroService;

    public ClienteController(ClienteService clienteService, SuministroService suministroService) {
        this.clienteService = clienteService;
        this.suministroService = suministroService;
    }

    @Operation(summary = "Clientes visibles para el usuario autenticado (todos si es ADMIN/MANAGER)")
    @GetMapping
    public List<ClienteDTO> listarClientes() {
        return clienteService.getClientesVisibles();
    }

    @Operation(summary = "Detalle de un cliente")
    @GetMapping("/{id}")
    public ClienteDTO obtenerCliente(@PathVariable Long id) {
        return clienteService.getClienteById(id);
    }

    @Operation(summary = "Clientes de un empleado")
    @GetMapping("/{empleadoId}/clientes")
    public List<ClienteDTO> clientesPorEmpleado(@PathVariable Long empleadoId) {
        return clienteService.getClientesByEmpleadoId(empleadoId);
    }

    @Operation(summary = "Crea un cliente asignado al usuario autenticado")
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearCliente(request));
    }

    @Operation(summary = "Crea un cliente asignado a un empleado concreto")
    @PostMapping("/{empleadoId}/clientes")
    public ResponseEntity<ClienteDTO> crearClienteParaEmpleado(@PathVariable Long empleadoId,
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearClienteParaEmpleado(empleadoId, request));
    }

    @Operation(summary = "Actualiza un cliente")
    @PutMapping("/{id}")
    public ClienteDTO actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return clienteService.actualizarCliente(id, request);
    }

    @Operation(summary = "Elimina un cliente y sus suministros")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    // ----- Suministros del cliente ---------------------------------------------

    @Operation(summary = "Suministros (CUPS) de un cliente")
    @GetMapping("/{id}/suministros")
    public List<SuministroDTO> suministros(@PathVariable Long id) {
        return suministroService.getSuministrosDeCliente(id);
    }

    @Operation(summary = "Añade un suministro a un cliente")
    @PostMapping("/{id}/suministros")
    public ResponseEntity<SuministroDTO> crearSuministro(@PathVariable Long id, @Valid @RequestBody SuministroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suministroService.crearSuministro(id, request));
    }
}
