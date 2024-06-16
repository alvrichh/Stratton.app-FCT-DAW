package com.StrattonApp.Backend.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.entities.Rol;
import com.StrattonApp.Backend.exceptions.ResourceNotFoundException;
import com.StrattonApp.Backend.repository.RolRepository;

@Service
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    public Rol createRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public Optional<Rol> getRolById(Long id) {
        return rolRepository.findById(id);
    }

    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    public Rol updateRol(Long id, Rol rolDetails) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol not found"));
        rol.setNombre(rolDetails.getNombre());
        return rolRepository.save(rol);
    }

    public void deleteRol(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol not found"));
        rolRepository.delete(rol);
    }
}
