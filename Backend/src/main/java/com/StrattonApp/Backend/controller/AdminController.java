package com.StrattonApp.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.repository.EmpleadoRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    // Endpoint para crear un nuevo empleado
    @PostMapping("/empleados")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Empleado createEmployee(@RequestBody Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    // Endpoint para obtener todos los empleados
    @GetMapping("/empleados")
    public List<Empleado> getAllEmployees() {
        return empleadoRepository.findAll();
    }

    // Endpoint para obtener un empleado por su ID
    @GetMapping("/empleados/{id}")
    public Optional<Empleado> getEmployeeById(@PathVariable Long id) {
        return empleadoRepository.findById(id);
    }

    // Endpoint para actualizar un empleado
    @PutMapping("/empleados/{id}")
    public Empleado updateEmployee(@PathVariable Long id, @RequestBody Empleado updatedEmployee) {
        return empleadoRepository.findById(id)
                .map(employee -> {
                    employee.setNombre(updatedEmployee.getNombre());
                    employee.setApellidos(updatedEmployee.getApellidos());
                    // Actualiza otros campos según sea necesario
                    return empleadoRepository.save(employee);
                })
                .orElse(null);
    }

    // Endpoint para eliminar un empleado por su ID
    @DeleteMapping("/empleados/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        empleadoRepository.deleteById(id);
    }
}
