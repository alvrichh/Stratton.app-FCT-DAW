package com.StrattonApp.Backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.StrattonApp.Backend.DTO.ClienteDTO;
import com.StrattonApp.Backend.entities.Cliente;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	
    @Query("SELECT c FROM Cliente c INNER JOIN c.suministros s WHERE s.cups = :cups")
    List<Cliente> findByCups(@Param("cups") String cups);
    
}
