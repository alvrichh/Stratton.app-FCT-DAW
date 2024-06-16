package com.StrattonApp.Backend.controller;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.mappers.EmpleadoMapper;
import com.StrattonApp.Backend.service.EmpleadoService;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
    @Autowired
    private EmpleadoService empleadoService;

    @PostMapping
    public EmpleadoDTO createEmpleado(@RequestBody EmpleadoDTO empleadoDTO) {
        Empleado empleado = EmpleadoMapper.toEntity(empleadoDTO);
        Empleado createdEmpleado = empleadoService.createEmpleado(empleado);
        return EmpleadoMapper.toDTO(createdEmpleado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> getEmpleadoById(@PathVariable Long id) {
        Optional<Empleado> empleado = empleadoService.getEmpleadoById(id);
        return empleado.map(value -> ResponseEntity.ok(EmpleadoMapper.toDTO(value)))
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<EmpleadoDTO> getAllEmpleados() {
        return empleadoService.getAllEmpleados().stream()
                              .map(EmpleadoMapper::toDTO)
                              .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> updateEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO empleadoDTO) {
        Empleado empleadoDetails = EmpleadoMapper.toEntity(empleadoDTO);
        Empleado updatedEmpleado = empleadoService.updateEmpleado(id, empleadoDetails);
        return ResponseEntity.ok(EmpleadoMapper.toDTO(updatedEmpleado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long id) {
        empleadoService.deleteEmpleado(id);
        return ResponseEntity.noContent().build();
    }
}
