package com.StrattonApp.Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.StrattonApp.Backend.entities.Comercializadora;

@Repository
public interface ComercializadoraRepository extends JpaRepository<Comercializadora, Long> {

    Optional<Comercializadora> findByCompaniaContratadaIgnoreCase(String companiaContratada);

    List<Comercializadora> findAllByOrderByCompaniaContratadaAsc();
}
