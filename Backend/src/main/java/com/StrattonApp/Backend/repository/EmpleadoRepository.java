package com.StrattonApp.Backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.StrattonApp.Backend.DTO.EmpleadoDTO;
import com.StrattonApp.Backend.entities.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

	/**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param email La dirección de correo electrónico del usuario.
     * @return Un Optional que contiene el usuario encontrado o es vacío si no se encuentra.
     */
    Optional<Empleado> findByEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username El nombre de usuario del usuario.
     * @return Un Optional que contiene el usuario encontrado o es vacío si no se encuentra.
     */
    Optional<Empleado> findByUsername(String username);

    /**
     * Verifica si existe un usuario con la dirección de correo electrónico dada.
     *
     * @param email La dirección de correo electrónico a verificar.
     * @return `true` si existe un usuario con la dirección de correo electrónico dada, `false` en caso contrario.
     */
    Boolean existsByEmail(String email);
}
