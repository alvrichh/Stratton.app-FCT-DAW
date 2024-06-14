package com.StrattonApp.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;
import com.StrattonApp.Backend.entities.Empleado;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Consulta personalizada para buscar clientes por el valor de cups en los suministros asociados
    @Query("SELECT c FROM Cliente c INNER JOIN c.suministros s WHERE s.cups = :cups")
    List<ClienteDTO> findByCups(@Param("cups") String cups);

    // Método que encuentra clientes por el ID del empleado
    List<Cliente> findByEmpleadoId(Long idEmpleado);

    // Contar clientes por el empleado asociado
    Long countByEmpleado(Empleado empleado);

    // Encontrar clientes por el objeto Empleado
    List<Cliente> findByEmpleado(Empleado empleado);
}
