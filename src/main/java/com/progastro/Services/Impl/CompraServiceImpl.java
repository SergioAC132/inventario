package com.progastro.Services.Impl;

import org.springframework.stereotype.Service;

import com.progastro.Services.CompraServiceBridge;
import com.progastro.inventario.models.DTO.CompraRequestDTO;
import com.progastro.inventario.models.DTO.CompraResponseDTO;
import com.progastro.inventario.models.Entities.Proveedor;
import com.progastro.inventario.repositories.CompraRepository;
import com.progastro.inventario.repositories.ProveedorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraServiceBridge {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public CompraResponseDTO registrarCompra(CompraRequestDTO request) {
        
        validarDatosProveedor(request.getProveedorId(), request.getNumeroFactura()); 
        
    }

    private void validarDatosProveedor(Long proveedorId, String numeroFactura) {

        Proveedor proveedor = proveedorRepository.findById(proveedorId).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
    }
    
}
