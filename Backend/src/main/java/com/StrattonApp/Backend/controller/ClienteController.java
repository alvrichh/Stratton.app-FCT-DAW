package com.StrattonApp.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.service.ClienteService;

@Controller
public class ClienteController {
	
    @Autowired
    private ClienteService clienteService; //servicio para manejar los clientes

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        // Recuperar todos los clientes de la base de datos
        List<ClienteDTO> clientesDTO = clienteService.obtenerClientesDTO();
        
        // Agregar la lista de clientes al modelo para pasarla a la vista
        model.addAttribute("clientes", clientesDTO);
        
        // Devolver el nombre de la vista del dashboard donde se mostrarán los clientes
        return "dashboard";
}

    @GetMapping("/nuevo-cliente")
    public String mostrarFormularioNuevoCliente(Model model) {
        return "formulario-nuevo-cliente"; // nombre de la plantilla HTML del formulario
    }

}
