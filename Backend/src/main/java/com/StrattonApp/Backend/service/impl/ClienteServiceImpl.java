package com.StrattonApp.Backend.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.DTO.request.ClienteRequest;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Comercializadora;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Estado;
import com.StrattonApp.Backend.entities.Suministro;
import com.StrattonApp.Backend.exceptions.ConflictException;
import com.StrattonApp.Backend.exceptions.ForbiddenException;
import com.StrattonApp.Backend.exceptions.GlobalException;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.repository.ComercializadoraRepository;
import com.StrattonApp.Backend.repository.EmpleadoRepository;
import com.StrattonApp.Backend.repository.SuministroRepository;
import com.StrattonApp.Backend.security.SecurityUtils;
import com.StrattonApp.Backend.service.ClienteService;

/**
 * Implementación de {@link ClienteService}.
 */
@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ComercializadoraRepository comercializadoraRepository;
    private final SuministroRepository suministroRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository, EmpleadoRepository empleadoRepository,
            ComercializadoraRepository comercializadoraRepository, SuministroRepository suministroRepository) {
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.comercializadoraRepository = comercializadoraRepository;
        this.suministroRepository = suministroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> getClientesVisibles() {
        Empleado actual = SecurityUtils.currentEmpleado();
        List<Cliente> clientes = actual.isJefe()
                ? clienteRepository.findAllByOrderByApellidosAscNombreAsc()
                : clienteRepository.findByEmpleadoIdOrderByApellidosAscNombreAsc(actual.getId());
        return clientes.stream().map(ClienteDTO::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> getClientesByEmpleadoId(Long empleadoId) {
        Empleado actual = SecurityUtils.currentEmpleado();
        if (!actual.isJefe() && !actual.getId().equals(empleadoId)) {
            throw new ForbiddenException("Solo puedes consultar tus propios clientes");
        }
        if (!empleadoRepository.existsById(empleadoId)) {
            throw new ResourceNotFoundException("No existe el empleado con el ID: " + empleadoId);
        }
        return clienteRepository.findByEmpleadoIdOrderByApellidosAscNombreAsc(empleadoId).stream()
                .map(ClienteDTO::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO getClienteById(Long clienteId) {
        return ClienteDTO.from(findClienteVisible(clienteId));
    }

    @Override
    public ClienteDTO crearCliente(ClienteRequest request) {
        Empleado actual = SecurityUtils.currentEmpleado();
        Long empleadoId = actual.getId();
        if (request.getEmpleadoId() != null && !request.getEmpleadoId().equals(actual.getId())) {
            if (!actual.isJefe()) {
                throw new ForbiddenException("Solo un administrador puede asignar clientes a otros empleados");
            }
            empleadoId = request.getEmpleadoId();
        }
        return crearClienteParaEmpleado(empleadoId, request);
    }

    @Override
    public ClienteDTO crearClienteParaEmpleado(Long empleadoId, ClienteRequest request) {
        Empleado actual = SecurityUtils.currentEmpleado();
        if (!actual.isJefe() && !actual.getId().equals(empleadoId)) {
            throw new ForbiddenException("Solo puedes crear clientes para ti mismo");
        }
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + empleadoId));

        validarUnicidad(request, null);
        validarMayoriaDeEdad(request.getFechaNacimiento());

        Cliente cliente = new Cliente();
        aplicarDatos(cliente, request);
        cliente.setEmpleado(empleado);
        cliente.setAsesoria(empleado.getAsesoria());
        if (cliente.getFechaSubidaContrato() == null) {
            cliente.setFechaSubidaContrato(LocalDate.now());
        }

        Comercializadora comercializadora = resolverComercializadora(request.getComercializadoraId(), request.getCompaniaContratada());
        cliente.setComercializadora(comercializadora);
        cliente = clienteRepository.save(cliente);

        if (request.getCups() != null && !request.getCups().isBlank()) {
            crearOActualizarSuministroPrincipal(cliente, request, comercializadora);
        }
        return ClienteDTO.from(clienteRepository.save(cliente));
    }

    @Override
    public ClienteDTO actualizarCliente(Long clienteId, ClienteRequest request) {
        Cliente cliente = findClienteVisible(clienteId);
        validarUnicidad(request, clienteId);
        validarMayoriaDeEdad(request.getFechaNacimiento());

        aplicarDatos(cliente, request);

        Empleado actual = SecurityUtils.currentEmpleado();
        if (request.getEmpleadoId() != null && actual.isJefe()
                && (cliente.getEmpleado() == null || !request.getEmpleadoId().equals(cliente.getEmpleado().getId()))) {
            Empleado nuevo = empleadoRepository.findById(request.getEmpleadoId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe el empleado con el ID: " + request.getEmpleadoId()));
            cliente.setEmpleado(nuevo);
            cliente.setAsesoria(nuevo.getAsesoria());
        }

        Comercializadora comercializadora = resolverComercializadora(request.getComercializadoraId(), request.getCompaniaContratada());
        if (comercializadora != null) {
            cliente.setComercializadora(comercializadora);
        }
        if (request.getCups() != null && !request.getCups().isBlank()) {
            crearOActualizarSuministroPrincipal(cliente, request, comercializadora != null ? comercializadora : cliente.getComercializadora());
        } else if (comercializadora != null && !cliente.getSuministros().isEmpty()) {
            cliente.getSuministros().get(0).setComercializadora(comercializadora);
        }
        return ClienteDTO.from(clienteRepository.save(cliente));
    }

    @Override
    public void eliminarCliente(Long clienteId) {
        Cliente cliente = findClienteVisible(clienteId);
        // Los suministros se eliminan en cascada
        clienteRepository.delete(cliente);
    }

    // ----- helpers -----------------------------------------------------------

    /** Devuelve el cliente si el usuario autenticado puede verlo (propio, o cualquiera si es jefe). */
    private Cliente findClienteVisible(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID: " + clienteId));
        Empleado actual = SecurityUtils.currentEmpleado();
        boolean propio = cliente.getEmpleado() != null && cliente.getEmpleado().getId().equals(actual.getId());
        if (!actual.isJefe() && !propio) {
            throw new ForbiddenException("Este cliente pertenece a otro empleado");
        }
        return cliente;
    }

    private void aplicarDatos(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.getNombre().trim());
        cliente.setApellidos(request.getApellidos().trim());
        cliente.setDni(request.getDni().trim().toUpperCase());
        cliente.setEmail(request.getEmail().trim().toLowerCase());
        cliente.setTelefono(request.getTelefono().trim());
        cliente.setDireccion(request.getDireccion() == null ? null : request.getDireccion().trim());
        cliente.setIban(normalizarIban(request.getIban()));
        if (request.getFechaSubidaContrato() != null) {
            cliente.setFechaSubidaContrato(request.getFechaSubidaContrato());
        }
        cliente.setFechaNacimiento(request.getFechaNacimiento());
    }

    private String normalizarIban(String iban) {
        if (iban == null || iban.isBlank()) {
            return null;
        }
        return iban.replace(" ", "").toUpperCase();
    }

    /** RN-3 / RF-3: el DNI y el teléfono identifican de forma única al cliente. */
    private void validarUnicidad(ClienteRequest request, Long clienteIdActual) {
        clienteRepository.findByDniIgnoreCase(request.getDni().trim())
                .filter(c -> !c.getIdCliente().equals(clienteIdActual))
                .ifPresent(c -> { throw new ConflictException("Ya existe un cliente con el DNI " + request.getDni()); });
        clienteRepository.findByTelefono(request.getTelefono().trim())
                .filter(c -> !c.getIdCliente().equals(clienteIdActual))
                .ifPresent(c -> { throw new ConflictException("Ya existe un cliente con el teléfono " + request.getTelefono()); });
    }

    /** RN-1: el titular del contrato debe ser mayor de edad. */
    private void validarMayoriaDeEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento != null && Period.between(fechaNacimiento, LocalDate.now()).getYears() < 18) {
            throw new GlobalException("El titular del contrato debe ser mayor de edad");
        }
    }

    private Comercializadora resolverComercializadora(Long id, String nombre) {
        if (id != null) {
            return comercializadoraRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la comercializadora con el ID: " + id));
        }
        if (nombre != null && !nombre.isBlank()) {
            return comercializadoraRepository.findByCompaniaContratadaIgnoreCase(nombre.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la comercializadora " + nombre));
        }
        return null;
    }

    /**
     * Crea el suministro principal del cliente con el CUPS indicado, o actualiza el existente.
     * Si el CUPS ya pertenece a otro cliente se devuelve 409.
     */
    private void crearOActualizarSuministroPrincipal(Cliente cliente, ClienteRequest request, Comercializadora comercializadora) {
        String cups = request.getCups().trim().toUpperCase();
        Suministro existente = suministroRepository.findById(cups).orElse(null);
        if (existente != null && (existente.getCliente() == null || !existente.getCliente().getIdCliente().equals(cliente.getIdCliente()))) {
            throw new ConflictException("El CUPS " + cups + " ya está registrado para otro cliente");
        }

        Suministro suministro;
        if (existente != null) {
            suministro = existente;
        } else if (!cliente.getSuministros().isEmpty()) {
            // El cliente cambia el CUPS de su suministro principal: el CUPS es clave primaria,
            // así que se sustituye el registro.
            Suministro principal = cliente.getSuministros().get(0);
            suministro = new Suministro(cups, principal.getEstado(), principal.getDireccion(), principal.getMensaje(),
                    principal.getPotencia(), cliente, principal.getComercializadora());
            cliente.removeSuministro(principal);
            suministroRepository.delete(principal);
            suministroRepository.flush();
            cliente.getSuministros().add(0, suministro);
        } else {
            suministro = new Suministro();
            suministro.setCups(cups);
            cliente.addSuministro(suministro);
        }

        String direccion = request.getDireccion() != null && !request.getDireccion().isBlank()
                ? request.getDireccion().trim()
                : (suministro.getDireccion() != null ? suministro.getDireccion() : "Sin dirección");
        suministro.setDireccion(direccion);
        if (request.getEstado() != null) {
            suministro.setEstado(request.getEstado());
        } else if (suministro.getEstado() == null) {
            suministro.setEstado(Estado.PENDIENTE);
        }
        if (request.getPotencia() != null) {
            suministro.setPotencia(request.getPotencia());
        }
        if (comercializadora != null) {
            suministro.setComercializadora(comercializadora);
        }
        suministroRepository.save(suministro);
    }
}
