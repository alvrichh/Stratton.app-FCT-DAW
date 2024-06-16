package com.StrattonApp.Backend.mappers;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.DTO.RolDTO;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Empleado;

public class EmpleadoMapper {
    public static EmpleadoDTO toDTO(Empleado empleado) {
        List<ClienteDTO> clienteDTOs = empleado.getClientes().stream()
                                               .map(ClienteMapper::toDTO)
                                               .collect(Collectors.toList());
        RolDTO rolDTO = RolMapper.toDTO(empleado.getRol());
        return new EmpleadoDTO(empleado.getId(), empleado.getNombre(), clienteDTOs, rolDTO);
    }

    public static Empleado toEntity(EmpleadoDTO empleadoDTO) {
        Empleado empleado = new Empleado(empleadoDTO.getId(), empleadoDTO.getNombre(), 
                                         new HashSet<>(), RolMapper.toEntity(empleadoDTO.getRol()));
        for (ClienteDTO clienteDTO : empleadoDTO.getClientes()) {
            Cliente cliente = ClienteMapper.toEntity(clienteDTO);
            cliente.setEmpleado(empleado);
            empleado.getClientes().add(cliente);
        }
        return empleado;
    }
}
