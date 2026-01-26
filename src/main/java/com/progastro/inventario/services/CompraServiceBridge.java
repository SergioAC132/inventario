package com.progastro.inventario.services;

import com.progastro.inventario.models.DTO.CompraRequestDTO;
import com.progastro.inventario.models.DTO.CompraResponseDTO;

public interface CompraServiceBridge {

    CompraResponseDTO registrarCompra(CompraRequestDTO request);
    
}
