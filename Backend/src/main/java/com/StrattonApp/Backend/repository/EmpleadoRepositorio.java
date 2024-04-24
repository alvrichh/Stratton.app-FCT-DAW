package com.StrattonApp.Backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.StrattonApp.Backend.entities.Empleado;

@Repository
public interface EmpleadoRepositorio extends CrudRepository<Empleado, Long> {

}
