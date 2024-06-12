package com.StrattonApp.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.service.ClienteService;

@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<ClienteDTO> listarTodosLosClientes() {
        return clienteService.getAllClientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable Long id) {
        ClienteDTO cliente = clienteService.getClienteById(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/{empleadoId}/clientes")
    public List<ClienteDTO> obtenerClientesPorEmpleadoId(@PathVariable Long empleadoId) {
        return clienteService.getClientesByEmpleadoId(empleadoId);
    }
    @PostMapping
    public ResponseEntity<ClienteDTO> guardarCliente(@RequestBody Cliente cliente) {
        ClienteDTO clienteDTO = clienteService.guardarCliente(cliente);
        return ResponseEntity.ok(clienteDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable Long id, @RequestBody Cliente detallesCliente) {
        ClienteDTO clienteDTO = clienteService.actualizarCliente(id, detallesCliente);
        return ResponseEntity.ok(clienteDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.ok().build();
    }
 
}
