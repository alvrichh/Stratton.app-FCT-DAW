/*
 * Paquete del controlador de la aplicación.
 */
package com.StrattonApp.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.StrattonApp.Backend.entities.Empleado;
import com.StrattonApp.Backend.repository.EmpleadoRepository;

import java.util.List;
import java.util.Optional;
/**
 * Controlador RESTful para operaciones administrativas relacionadas con empleados.
 * Proporciona endpoints para la gestión de empleados como crear, obtener, actualizar y eliminar.
 */
@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    /**
     * Crea un nuevo empleado.
     *
     * @param empleado Los detalles del empleado a crear.
     * @return El empleado creado.
     */
    @PostMapping("/empleados")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Empleado createEmployee(@RequestBody Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    /**
     * Obtiene todos los empleados.
     *
     * @return Una lista de todos los empleados.
     */
    @GetMapping("/empleados")
    public List<Empleado> getAllEmployees() {
        return empleadoRepository.findAll();
    }

    /**
     * Obtiene un empleado por su ID.
     *
     * @param id El ID del empleado.
     * @return El empleado correspondiente al ID, envuelto en un Optional.
     */
    @GetMapping("/empleados/{id}")
    public Optional<Empleado> getEmployeeById(@PathVariable Long id) {
        return empleadoRepository.findById(id);
    }

    /**
     * Actualiza un empleado existente.
     *
     * @param id El ID del empleado a actualizar.
     * @param updatedEmployee Los detalles actualizados del empleado.
     * @return El empleado actualizado, o null si no se encontró.
     */
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

    /**
     * Elimina un empleado por su ID.
     *
     * @param id El ID del empleado a eliminar.
     */
    @DeleteMapping("/empleados/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        empleadoRepository.deleteById(id);
    }
}
