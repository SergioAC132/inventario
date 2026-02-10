package com.progastro.inventario.services.Impl;

import org.springframework.stereotype.Service;

import com.progastro.inventario.models.DTO.ProductoRequestDTO;
import com.progastro.inventario.models.DTO.ProductoResponseDTO;
import com.progastro.inventario.repositories.ProductoRepository;
import com.progastro.inventario.services.ProductoServiceBridge;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoServiceBridge {
    
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public ProductoResponseDTO registrarProducto(ProductoRequestDTO request) {
        return null;
    }

}
