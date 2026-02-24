package com.progastro.inventario.mappers;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.RolResponseDTO;
import com.progastro.inventario.models.Entities.Rol;

@Component
public class RolMapper {
    
    public RolResponseDTO toResponse(Rol rol) {
        return new RolResponseDTO(
            rol.getIdRol(),
            rol.getNombre()
        );
    }
}
