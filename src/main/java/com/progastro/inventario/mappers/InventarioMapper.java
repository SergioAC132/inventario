package com.progastro.inventario.mappers;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.InventarioResponseDTO;
import com.progastro.inventario.models.Entities.Inventario;

@Component
public class InventarioMapper {
    
    public InventarioResponseDTO toResponse(Inventario inventario) {
        return new InventarioResponseDTO(
            inventario.getIdInventario(),
            inventario.getLote(),
            inventario.getCantidadDisponible(),
            inventario.getFechaCaducidad(),
            inventario.getActive()
        );
    }
}
