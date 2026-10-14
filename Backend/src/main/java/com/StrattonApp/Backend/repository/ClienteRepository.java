package com.StrattonApp.Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Comercializadora;
import com.StrattonApp.Backend.entities.Empleado;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByEmpleadoIdOrderByApellidosAscNombreAsc(Long idEmpleado);

    List<Cliente> findAllByOrderByApellidosAscNombreAsc();

    Long countByEmpleado(Empleado empleado);

    Optional<Cliente> findByDniIgnoreCase(String dni);

    Optional<Cliente> findByTelefono(String telefono);

    boolean existsByDniIgnoreCase(String dni);

    boolean existsByTelefono(String telefono);

    boolean existsByComercializadora(Comercializadora comercializadora);
}
