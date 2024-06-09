package com.StrattonApp.Backend.service;

import java.util.List;
import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;

public interface ClienteService {
    List<ClienteDTO> obtenerClientesDTO();
    ClienteDTO getClienteDetallesById(Long idCliente);
}
