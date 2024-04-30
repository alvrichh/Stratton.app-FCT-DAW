package com.StrattonApp.Backend.repository;

import com.StrattonApp.Backend.entities.Asesoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsesoriaRepository extends JpaRepository<Asesoria, Long> {

    /**
     * Busca una asesoría por su ID.
     *
     * @param id El ID de la asesoría.
     * @return Un Optional que contiene la asesoría encontrada o es vacío si no se encuentra.
     */
    Optional<Asesoria> findById(Long id);

    /**
     * Busca una asesoría por su dirección.
     *
     * @param direccion La dirección de la asesoría.
     * @return Un Optional que contiene la asesoría encontrada o es vacío si no se encuentra.
     */
    Optional<Asesoria> findByDireccion(String direccion);

    /**
     * Busca una asesoría por su nombre.
     *
     * @param nombre El nombre de la asesoría.
     * @return Un Optional que contiene la asesoría encontrada o es vacío si no se encuentra.
     */
    Optional<Asesoria> findByNombre(String nombre);

    /**
     * Verifica si existe una asesoría con la dirección dada.
     *
     * @param direccion La dirección de la asesoría a verificar.
     * @return `true` si existe una asesoría con la dirección dada, `false` en caso contrario.
     */
    Boolean existsByDireccion(String direccion);
}
