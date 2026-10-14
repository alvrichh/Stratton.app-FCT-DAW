package com.StrattonApp.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.StrattonApp.Backend.entities.Suministro;

@Repository
public interface SuministroRepository extends JpaRepository<Suministro, String> {

    List<Suministro> findByClienteIdClienteOrderByCupsAsc(Long idCliente);

    List<Suministro> findByClienteEmpleadoIdOrderByCupsAsc(Long idEmpleado);
}
