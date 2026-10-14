package com.StrattonApp.Backend.service;

import java.util.List;

import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.DTO.request.SuministroRequest;

/**
 * Gestión de los suministros (CUPS) de un cliente.
 */
public interface SuministroService {

    List<SuministroDTO> getSuministrosDeCliente(Long clienteId);

    SuministroDTO crearSuministro(Long clienteId, SuministroRequest request);

    SuministroDTO actualizarSuministro(String cups, SuministroRequest request);

    void eliminarSuministro(String cups);
}
