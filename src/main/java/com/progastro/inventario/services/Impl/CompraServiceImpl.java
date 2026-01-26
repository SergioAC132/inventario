package com.progastro.inventario.services.Impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.models.DTO.CompraProductoRequestDTO;
import com.progastro.inventario.models.DTO.CompraRequestDTO;
import com.progastro.inventario.models.DTO.CompraResponseDTO;
import com.progastro.inventario.models.Entities.Compra;
import com.progastro.inventario.models.Entities.CompraProductos;
import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Proveedor;
import com.progastro.inventario.models.Enums.EstatusCompra;
import com.progastro.inventario.repositories.CompraProductosRepository;
import com.progastro.inventario.repositories.CompraRepository;
import com.progastro.inventario.repositories.ProveedorRepository;
import com.progastro.inventario.services.CompraServiceBridge;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraServiceBridge {

    private final CompraProductosRepository compraProductosRepository;
    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public CompraResponseDTO registrarCompra(CompraRequestDTO request) {
        
        Proveedor proveedor = validarDatosProveedor(request.getProveedorId(), request.getNumeroFactura()); 
        

        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setProveedor(proveedor);
        compra.setNumeroFactura(request.getNumeroFactura());
        compra.setEstatus(EstatusCompra.REGISTRADA);

        BigDecimal totalCompra = request.getProductos()
                    .stream()
                    .map(CompraProductoRequestDTO::getCostoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        request.getProductos().forEach(e -> {
            Inventario inventario = obtenerOCrearInventario(e);

            CompraProductos cp = new CompraProductos();

            cp.setCompra(compra);
            cp.setInventario(inventario);
            cp.setCantidad(e.getCantidad());
            cp.setCostoTotal(e.getCostoTotal());
            cp.setSubtotal(e.getCostoUnitario() != null ? e.getCostoUnitario() : null);

            compraProductosRepository.save(cp);
        });

        compra.setTotal(totalCompra);
        compraRepository.save(compra);

        return mapper.mapToResponse(compra);
    }

    private Proveedor validarDatosProveedor(Long proveedorId, String numeroFactura) {

        Proveedor proveedor = proveedorRepository.findById(proveedorId).orElseThrow(() ->
            new ResourceNotFoundException(("Proveedor no encontrado con id" + proveedorId).toString())
        );

        boolean facturaDuplicada = compraRepository.existsByNumeroFacturaAndProveedor(numeroFactura, proveedor);

        if (facturaDuplicada) {
            throw new ValidationException(
                "Ya existe una compra con ese número de factura para este proveedor"
            );
        }

        return proveedor;
    }
    
}

