package com.progastro.inventario.services;

import org.springframework.data.domain.Page;

import com.progastro.inventario.models.DTO.ProductoRequestDTO;
import com.progastro.inventario.models.DTO.ProductoResponseDTO;

public interface ProductoServiceBridge {

    ProductoResponseDTO registrarProducto(ProductoRequestDTO request);

    Page<ProductoResponseDTO> listarProductos(String marca, String nombre, String codigo, int page, int size);
    
}
