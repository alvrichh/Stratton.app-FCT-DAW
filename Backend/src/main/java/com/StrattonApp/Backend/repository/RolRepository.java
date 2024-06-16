package com.StrattonApp.Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.StrattonApp.Backend.entities.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
}
