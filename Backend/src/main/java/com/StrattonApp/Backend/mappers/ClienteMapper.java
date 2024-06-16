package com.StrattonApp.Backend.mappers;
import java.util.Set;
import java.util.stream.Collectors;
import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.entities.Cliente;


public class ClienteMapper {
	
    public static ClienteDTO toDTO(Cliente cliente) {
        Set<SuministroDTO> suministroDTOs = cliente.getSuministros().stream()
                                                   .map(SuministroMapper::toDTO)
                                                   .collect(Collectors.toSet());
        return new ClienteDTO(cliente.getId(), cliente.getNombre(), suministroDTOs);
    }

    public static Cliente toEntity(ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();
        cliente.setId(clienteDTO.getId());
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setSuministros(clienteDTO.getSuministros().stream()
                                          .map(SuministroMapper::toEntity)
                                          .collect(Collectors.toSet()));
        return cliente;
    }
}
