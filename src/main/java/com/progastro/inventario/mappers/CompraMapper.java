package com.progastro.inventario.mappers;

import java.math.BigDecimal;

import com.progastro.inventario.models.DTO.CompraResponseDTO;
import com.progastro.inventario.models.Entities.Compra;

public class CompraMapper {
    
    public CompraResponseDTO toResponse(Compra compra, ) {

        return new CompraResponseDTO(
            compra.getIdCompra(),
            compra.getFecha(),
            compra.getProveedor().getIdProveedor()
            compra.getNumeroFactura(),
            compra.getEstatus(),
            total,
        );
    }

    
}
