package com.StrattonApp.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.service.ClienteService;

/**
 * Controlador RESTful para operaciones CRUD relacionadas con clientes.
 * Proporciona endpoints para listar, obtener, guardar, actualizar y eliminar clientes.
 */
@RestController
@RequestMapping("/api/v2/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Obtiene todos los clientes.
     *
     * @return Una lista de todos los clientes.
     */
    @GetMapping
    public List<ClienteDTO> listarTodosLosClientes() {
        return clienteService.getAllClientes();
    }

    /**
     * Obtiene un cliente por su ID.
     *
     * @param id El ID del cliente.
     * @return El cliente correspondiente al ID, envuelto en ResponseEntity.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable Long id) {
        ClienteDTO cliente = clienteService.getClienteById(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Obtiene todos los clientes asociados a un empleado por su ID.
     *
     * @param empleadoId El ID del empleado.
     * @return Una lista de todos los clientes asociados al empleado, envuelto en ResponseEntity.
     */
    @GetMapping("/{empleadoId}/clientes")
    public List<ClienteDTO> obtenerClientesPorEmpleadoId(@PathVariable Long empleadoId) {
        return clienteService.getClientesByEmpleadoId(empleadoId);
    }


    /**
     * Guarda un nuevo cliente.
     *
     * @param cliente Los detalles del cliente a guardar.
     * @return El cliente guardado, envuelto en ResponseEntity.
     */
    @PostMapping
    public ResponseEntity<?> guardarCliente(@RequestBody Cliente cliente) {
        ClienteDTO clienteDTO = clienteService.guardarCliente(cliente);
        return ResponseEntity.ok(clienteDTO);
    }

    /**
     * Guarda un nuevo cliente asociado a un empleado específico.
     *
     * @param empleadoId El ID del empleado.
     * @param cliente Los detalles del cliente a guardar.
     * @return El cliente guardado, envuelto en ResponseEntity.
     */
    @PostMapping("/{empleadoId}/clientes")
    public ResponseEntity<?> guardarClientePorEmpleado(@PathVariable Long empleadoId, @RequestBody Cliente cliente) {
        ClienteDTO clienteDTO = clienteService.guardarClientePorEmpleado(empleadoId, cliente);
        return ResponseEntity.ok(clienteDTO);
    }

    /**
     * Actualiza un cliente existente por su ID.
     *
     * @param id             El ID del cliente a actualizar.
     * @param detallesCliente Los detalles actualizados del cliente.
     * @return El cliente actualizado, envuelto en ResponseEntity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable Long id, @RequestBody Cliente detallesCliente) {
        ClienteDTO clienteDTO = clienteService.actualizarCliente(id, detallesCliente);
        return ResponseEntity.ok(clienteDTO);
    }

    /**
     * Elimina un cliente por su ID.
     *
     * @param id El ID del cliente a eliminar.
     * @return ResponseEntity indicando éxito o fallo de la operación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.ok().build();
    }
}
