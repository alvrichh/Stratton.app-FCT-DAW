package com.StrattonApp.Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.StrattonApp.Backend.entities.Asesoria;

@Repository
public interface AsesoriaRepository extends JpaRepository<Asesoria, Long> {

    Optional<Asesoria> findByNombreIgnoreCase(String nombre);

    List<Asesoria> findAllByOrderByNombreAsc();
}
