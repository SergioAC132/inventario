package com.progastro.inventario.services.Impl;

import org.springframework.stereotype.Service;

import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.repositories.InventarioRepository;
import com.progastro.inventario.repositories.ProductoRepository;
import com.progastro.inventario.services.InventarioServiceBridge;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioServiceBridge {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public Inventario obtenerOCreInventario(CompraProductoRequestDTO dto) {
        
    }
    
}
