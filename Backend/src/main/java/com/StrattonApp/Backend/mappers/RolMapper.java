package com.StrattonApp.Backend.mappers;

import com.StrattonApp.Backend.DTO.RolDTO;
import com.StrattonApp.Backend.entities.Rol;

public class RolMapper {
    public static RolDTO toDTO(Rol rol) {
        return new RolDTO(rol.getId(), rol.getNombre());
    }

    public static Rol toEntity(RolDTO rolDTO) {
        return new Rol(rolDTO.getId(), rolDTO.getNombre());
    }
}
