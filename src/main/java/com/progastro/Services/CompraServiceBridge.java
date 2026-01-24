package com.progastro.Services;

import com.progastro.inventario.models.DTO.CompraRequestDTO;
import com.progastro.inventario.models.DTO.CompraResponseDTO;

public interface CompraServiceBridge {

    CompraResponseDTO registrarCompra(CompraRequestDTO request);
    
}
