package com.StrattonApp.Backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.StrattonApp.Backend.entities.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	
	List<Cliente> findByCUPS(String nombre);
}
