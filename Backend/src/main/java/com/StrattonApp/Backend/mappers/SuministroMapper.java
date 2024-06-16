package com.StrattonApp.Backend.mappers;

import com.StrattonApp.Backend.DTO.SuministroDTO;
import com.StrattonApp.Backend.entities.Suministro;

public class SuministroMapper {
    public static SuministroDTO toDTO(Suministro suministro) {
        return new SuministroDTO(suministro.getId(), suministro.getNombre(), suministro.getPrecio());
    }

    public static Suministro toEntity(SuministroDTO suministroDTO) {
        Suministro suministro = new Suministro();
        suministro.setId(suministroDTO.getId());
        suministro.setNombre(suministroDTO.getNombre());
        suministro.setPrecio(suministroDTO.getPrecio());
        return suministro;
    }
}
