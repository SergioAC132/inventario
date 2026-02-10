package com.progastro.inventario.services;

import com.progastro.inventario.models.DTO.ProductoRequestDTO;
import com.progastro.inventario.models.DTO.ProductoResponseDTO;

public interface ProductoServiceBridge {

    ProductoResponseDTO registrarProducto(ProductoRequestDTO request);
    
}
