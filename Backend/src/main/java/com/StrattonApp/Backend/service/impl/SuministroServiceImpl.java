package com.StrattonApp.Backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.DTO.request.SuministroRequest;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Comercializadora;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.entities.Estado;
import com.StrattonApp.Backend.entities.Suministro;
import com.StrattonApp.Backend.exceptions.ConflictException;
import com.StrattonApp.Backend.exceptions.ForbiddenException;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.ClienteRepository;
import com.StrattonApp.Backend.repository.ComercializadoraRepository;
import com.StrattonApp.Backend.repository.SuministroRepository;
import com.StrattonApp.Backend.security.SecurityUtils;
import com.StrattonApp.Backend.service.SuministroService;

/**
 * Implementación de {@link SuministroService}.
 */
@Service
@Transactional
public class SuministroServiceImpl implements SuministroService {

    private final SuministroRepository suministroRepository;
    private final ClienteRepository clienteRepository;
    private final ComercializadoraRepository comercializadoraRepository;

    public SuministroServiceImpl(SuministroRepository suministroRepository, ClienteRepository clienteRepository,
            ComercializadoraRepository comercializadoraRepository) {
        this.suministroRepository = suministroRepository;
        this.clienteRepository = clienteRepository;
        this.comercializadoraRepository = comercializadoraRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuministroDTO> getSuministrosDeCliente(Long clienteId) {
        findClienteVisible(clienteId);
        return suministroRepository.findByClienteIdClienteOrderByFechaAltaAscCupsAsc(clienteId).stream()
                .map(SuministroDTO::from).collect(Collectors.toList());
    }

    @Override
    public SuministroDTO crearSuministro(Long clienteId, SuministroRequest request) {
        Cliente cliente = findClienteVisible(clienteId);
        String cups = request.getCups().trim().toUpperCase();
        if (suministroRepository.existsById(cups)) {
            throw new ConflictException("El CUPS " + cups + " ya está registrado");
        }
        Suministro suministro = new Suministro();
        suministro.setCups(cups);
        aplicar(suministro, request);
        cliente.addSuministro(suministro);
        suministro = suministroRepository.save(suministro);
        clienteRepository.save(cliente);
        return SuministroDTO.from(suministro);
    }

    @Override
    public SuministroDTO actualizarSuministro(String cups, SuministroRequest request) {
        Suministro suministro = findSuministroVisible(cups);
        aplicar(suministro, request);
        return SuministroDTO.from(suministroRepository.save(suministro));
    }

    @Override
    public void eliminarSuministro(String cups) {
        Suministro suministro = findSuministroVisible(cups);
        Cliente cliente = suministro.getCliente();
        cliente.removeSuministro(suministro);
        suministroRepository.delete(suministro);
    }

    // ----- helpers -----------------------------------------------------------

    private Cliente findClienteVisible(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con el ID: " + clienteId));
        comprobarAcceso(cliente);
        return cliente;
    }

    private Suministro findSuministroVisible(String cups) {
        Suministro suministro = suministroRepository.findById(cups.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el suministro con CUPS " + cups));
        comprobarAcceso(suministro.getCliente());
        return suministro;
    }

    private void comprobarAcceso(Cliente cliente) {
        Empleado actual = SecurityUtils.currentEmpleado();
        boolean propio = cliente.getEmpleado() != null && cliente.getEmpleado().getId().equals(actual.getId());
        if (!actual.isJefe() && !propio) {
            throw new ForbiddenException("Este cliente pertenece a otro empleado");
        }
    }

    private void aplicar(Suministro suministro, SuministroRequest request) {
        suministro.setDireccion(request.getDireccion().trim());
        suministro.setEstado(request.getEstado() != null ? request.getEstado() : (suministro.getEstado() != null ? suministro.getEstado() : Estado.PENDIENTE));
        suministro.setMensaje(request.getMensaje());
        suministro.setPotencia(request.getPotencia());
        Comercializadora comercializadora = null;
        if (request.getComercializadoraId() != null) {
            comercializadora = comercializadoraRepository.findById(request.getComercializadoraId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la comercializadora con el ID: " + request.getComercializadoraId()));
        } else if (request.getCompaniaContratada() != null && !request.getCompaniaContratada().isBlank()) {
            comercializadora = comercializadoraRepository.findByCompaniaContratadaIgnoreCase(request.getCompaniaContratada().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la comercializadora " + request.getCompaniaContratada()));
        }
        if (comercializadora != null) {
            suministro.setComercializadora(comercializadora);
        }
    }
}
