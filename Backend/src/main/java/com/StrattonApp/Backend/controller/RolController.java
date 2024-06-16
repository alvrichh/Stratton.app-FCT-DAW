package com.StrattonApp.Backend.controller;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StrattonApp.Backend.DTO.RolDTO;
import com.StrattonApp.Backend.entities.Rol;
import com.StrattonApp.Backend.mappers.RolMapper;
import com.StrattonApp.Backend.service.RolService;

@RestController
@RequestMapping("/api/roles")
public class RolController {
    @Autowired
    private RolService rolService;

    @PostMapping
    public RolDTO createRol(@RequestBody RolDTO rolDTO) {
        Rol rol = RolMapper.toEntity(rolDTO);
        Rol createdRol = rolService.createRol(rol);
        return RolMapper.toDTO(createdRol);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> getRolById(@PathVariable Long id) {
        Optional<Rol> rol = rolService.getRolById(id);
        return rol.map(value -> ResponseEntity.ok(RolMapper.toDTO(value)))
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<RolDTO> getAllRoles() {
        return rolService.getAllRoles().stream()
                         .map(RolMapper::toDTO)
                         .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> updateRol(@PathVariable Long id, @RequestBody RolDTO rolDTO) {
        Rol rolDetails = RolMapper.toEntity(rolDTO);
        Rol updatedRol = rolService.updateRol(id, rolDetails);
        return ResponseEntity.ok(RolMapper.toDTO(updatedRol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        rolService.deleteRol(id);
        return ResponseEntity.noContent().build();
    }
}
