package com.StrattonApp.Backend.controller;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Role;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.repository.SuministroRepository;
import com.StrattonApp.Backend.service.EmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador RESTful para operaciones relacionadas con empleados.
 * Proporciona endpoints para listar, crear, actualizar, eliminar empleados,
 * obtener el perfil de empleado, obtener clientes asociados a un empleado,
 * obtener el total de clientes de un empleado y obtener suministros de clientes de un empleado.
 */
@RestController
@RequestMapping("/api/v2/empleados")
@CrossOrigin(origins = "http://localhost:80")
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);

    @Autowired
    private EmpleadoRepository empleadoRepositorio;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private SuministroRepository suministroRepository;

    @Autowired
    private EmpleadoService empleadoService;

    /**
     * Endpoint para listar todos los empleados.
     *
     * @return Lista de todos los empleados
     */
    @GetMapping
    @PreAuthorize("hasAuthority(Role.ADMIN.toString())")
    public List<EmpleadoDTO> listarTodosLosEmpleados() {
        logger.info("Endpoint: GET /empleados");
        return empleadoService.getAllUsers();
    }

    /**
     * Endpoint para guardar un nuevo empleado.
     *
     * @param empleado Datos del nuevo empleado a guardar
     * @return Empleado guardado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EmpleadoDTO guardarEmpleado(@RequestBody Empleado empleado) {
        logger.info("Endpoint: POST /empleados");
        Empleado savedEmpleado = empleadoRepositorio.save(empleado);
        return empleadoService.convertToDTO(savedEmpleado);
    }

    /**
     * Endpoint para obtener un empleado por su ID.
     *
     * @param id ID del empleado a buscar
     * @return Empleado encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorId(@PathVariable Long id) {
        logger.info("Endpoint: GET /empleados/{}", id);
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + id));
        EmpleadoDTO empleadoDTO = empleadoService.convertToDTO(empleado);
        return ResponseEntity.ok(empleadoDTO);
    }

    /**
     * Endpoint para actualizar los datos de un empleado.
     *
     * @param id             ID del empleado a actualizar
     * @param detallesEmpleado Nuevos datos del empleado
     * @return Empleado actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody Empleado detallesEmpleado) {
        logger.info("Endpoint: PUT /empleados/{}", id);
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + id));

        // Actualizar los datos básicos del empleado
        empleado.setNombre(detallesEmpleado.getNombre());
        empleado.setApellidos(detallesEmpleado.getApellidos());
        empleado.setEmail(detallesEmpleado.getEmail());
        empleado.setUsername(detallesEmpleado.getUsername());
        empleado.setPassword(detallesEmpleado.getPassword());
        empleado.setRoles(detallesEmpleado.getRoles());
        empleado.setAsesoria(detallesEmpleado.getAsesoria());

        // Obtener la lista actual de clientes asociados al empleado
        Set<Cliente> clientesActuales = empleado.getClientes();

        // Actualizar la lista de clientes asociados al empleado
        Set<Cliente> nuevosClientes = detallesEmpleado.getClientes();
        if (nuevosClientes != null) {
            // Eliminar clientes que ya no están en la nueva lista
            clientesActuales.removeIf(cliente -> !nuevosClientes.contains(cliente));

            // Agregar nuevos clientes que no estaban en la lista original
            for (Cliente cliente : nuevosClientes) {
                if (!clientesActuales.contains(cliente)) {
                    cliente.setEmpleado(empleado);
                    clientesActuales.add(cliente);
                }
            }
        }

        // Guardar los cambios en la colección de clientes
        empleado.setClientes(clientesActuales);
        Empleado empleadoActualizado = empleadoRepositorio.save(empleado);

        EmpleadoDTO empleadoDTO = empleadoService.convertToDTO(empleadoActualizado);
        return ResponseEntity.ok(empleadoDTO);
    }

    /**
     * Endpoint para eliminar un empleado por su ID.
     *
     * @param id ID del empleado a eliminar
     * @return Confirmación de eliminación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> eliminarEmpleado(@PathVariable Long id) {
        logger.info("Endpoint: DELETE /empleados/{}", id);
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + id));

        empleadoRepositorio.delete(empleado);
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminar", Boolean.TRUE);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint para obtener el perfil del empleado autenticado.
     *
     * @param authentication Información de autenticación del empleado
     * @return Perfil del empleado autenticado
     */
    @GetMapping("/perfil")
    public ResponseEntity<EmpleadoDTO> obtenerPerfilEmpleado(Authentication authentication) {
        String usuario = authentication.getName();
        logger.info("Endpoint: GET /empleados/perfil - Usuario autenticado: {}", usuario);
        Empleado empleado = empleadoRepositorio.findByUsername(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el empleado con el usuario: " + usuario));
        EmpleadoDTO empleadoDTO = empleadoService.convertToDTO(empleado);
        return ResponseEntity.ok(empleadoDTO);
    }

    /**
     * Endpoint para obtener el número total de clientes que tiene un empleado.
     *
     * @param id ID del empleado
     * @return Número total de clientes del empleado
     */
    @GetMapping("/{id}/total-clientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> obtenerTotalClientesPorEmpleado(@PathVariable Long id) {
        logger.info("Endpoint: GET /empleados/{}/total-clientes", id);
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + id));

        Long totalClientes = clienteRepository.countByEmpleado(empleado);
        return ResponseEntity.ok(totalClientes);
    }

    /**
     * Endpoint para obtener los suministros de todos los clientes de un empleado.
     *
     * @param id ID del empleado
     * @return Lista de suministros de los clientes del empleado
     */
    @GetMapping("/{id}/suministros-clientes")    
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> obtenerSuministrosClientesPorEmpleado(@PathVariable Long id) {
        logger.info("Endpoint: GET /empleados/{}/suministros-clientes", id);
        Empleado empleado = empleadoRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + id));

        List<Cliente> clientes = clienteRepository.findByEmpleadoId(id);
        List<SuministroDTO> suministrosClientes = clientes.stream()
                .flatMap(cliente -> cliente.getSuministros().stream())
                .map(suministro -> new SuministroDTO(suministro.getCups(), suministro.getDireccion(), suministro.getComercializadora().getCompaniaContratada(), suministro.getEstado(), null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(suministrosClientes);
    }
    /*
     *   @PostMapping("/{id}/clientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cliente> agregarClienteAEmpleado(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        logger.info("Endpoint: POST /empleados/{}/clientes", id);
        Cliente clienteGuardado = empleadoService.agregarClienteAEmpleado(id, clienteDTO);
        return ResponseEntity.ok(clienteGuardado);
    }
     
  
    */
    @PostMapping("/{id}/clientes")
    public ResponseEntity<Cliente> agregarClienteAEmpleado(
            @PathVariable Long id, 
            @RequestBody Cliente cliente) {

        if (cliente.getDireccion() == null || cliente.getDireccion().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Cliente createdCliente = empleadoService.agregarClienteAEmpleado(id, cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCliente);
    }
     
    /**
     * Método getter para obtener el repositorio de suministros.
     *
     * @return Repositorio de suministros
     */
    public SuministroRepository getSuministroRepository() {
        return suministroRepository;
    }

    /**
     * Método setter para establecer el repositorio de suministros.
     *
     * @param suministroRepository Repositorio de suministros a establecer
     */
    public void setSuministroRepository(SuministroRepository suministroRepository) {
        this.suministroRepository = suministroRepository;
    }
}
