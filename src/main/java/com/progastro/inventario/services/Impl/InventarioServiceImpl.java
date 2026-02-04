package com.progastro.inventario.services.Impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.models.DTO.CompraProductoRequestDTO;
import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Producto;
import com.progastro.inventario.repositories.InventarioRepository;
import com.progastro.inventario.repositories.ProductoRepository;
import com.progastro.inventario.services.InventarioServiceBridge;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioServiceBridge {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public Inventario obtenerOCrearInventario(CompraProductoRequestDTO dto) {

        Producto producto = productoRepository.findById(dto.getProductoId()).orElseThrow(() ->
            new ResourceNotFoundException("Producto no encontrado con id" + dto.getProductoId())
        );

        Optional<Inventario> existente = inventarioRepository.findByProductoAndLoteAndFechaCaducidad(producto, dto.getLote(), dto.getFechaCaducidad());

        if (existente.isPresent()) {
            Inventario inv = existente.get();
            inv.setCantidadDisponible(inv.getCantidadDisponible() + dto.getCantidad());
            return inventarioRepository.save(inv);
        }

        Inventario nuevo = new Inventario();
        nuevo.setProducto(producto);
        nuevo.setLote(dto.getLote());
        nuevo.setCantidadDisponible(dto.getCantidad());
        nuevo.setFechaCaducidad(dto.getFechaCaducidad());

        return inventarioRepository.save(nuevo);
    }
    
}
