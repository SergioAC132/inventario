package com.progastro.inventario.services.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.mappers.CompraMapper;
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
import com.progastro.inventario.services.InventarioServiceBridge;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraServiceBridge {

    private final CompraProductosRepository compraProductosRepository;
    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final InventarioServiceBridge inventarioService;
    private final CompraMapper compraMapper;

    @Override
    @Transactional
    public CompraResponseDTO registrarCompra(CompraRequestDTO request) {

        List<CompraProductos> listaProductos = new ArrayList<>();
        Proveedor proveedor = validarDatosProveedor(request.getProveedorId(), request.getNumeroFactura());
        

        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setProveedor(proveedor);
        compra.setNumeroFactura(request.getNumeroFactura());
        compra.setEstatus(request.getEstatus().equals("") ? EstatusCompra.REGISTRADA : request.getEstatus());

        BigDecimal totalCompra = request.getProductos()
                    .stream()
                    .map(CompraProductoRequestDTO::getCostoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        request.getProductos().forEach(e -> {
            Inventario inventario = inventarioService.obtenerOCrearInventario(e);

            CompraProductos cp = new CompraProductos();

            cp.setCompra(compra);
            cp.setInventario(inventario);
            cp.setCantidad(e.getCantidad());
            cp.setCostoTotal(e.getCostoTotal());
            cp.setSubtotal(e.getCostoUnitario() != null ? e.getCostoUnitario() : null);

            listaProductos.add(cp);
            //compraProductosRepository.save(cp);
        });

        compra.setTotal(totalCompra);
        compra.setProductos(listaProductos);
        compraRepository.save(compra);
        listaProductos.forEach(lp -> compraProductosRepository.save(lp));

        return compraMapper.toResponse(compra);
    }

    private Proveedor validarDatosProveedor(Long proveedorId, String numeroFactura) {

        Proveedor proveedor = proveedorRepository.findById(proveedorId).orElseThrow(() ->
            new ResourceNotFoundException(("Proveedor no encontrado con id" + proveedorId))
        );

        boolean facturaDuplicada = compraRepository.existsByNumeroFacturaAndProveedor(numeroFactura, proveedor);

        if (facturaDuplicada) {
            throw new ValidationException(
                "Ya existe una compra con ese número de factura para este proveedor"
            );
        }

        return proveedor;
    }

    @Override
    public Page<CompraResponseDTO> listarCompras(String proveedor, String estatus, LocalDate fechaInicio, LocalDate fechaFin, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());

        Page<Compra> compras = compraRepository.findByFiltros(proveedor, estatus, fechaInicio, fechaFin, pageable);

        compras.forEach(c -> c.setProductos(compraProductosRepository.findByCompra(c)));
        
        return compras.map(compraMapper::toResponse);
    }

    @Override
    @Transactional
    public CompraResponseDTO editarCompra (Long idCompra, CompraRequestDTO request) {

        Proveedor proveedor = validarDatosProveedor(request.getProveedorId(), request.getNumeroFactura());
        

        Compra compra = compraRepository.findById(idCompra).orElseThrow(() ->
            new ResourceNotFoundException(("Compra no encontrada con id" + idCompra))
        );
        compra.setProveedor(proveedor);
        compra.setNumeroFactura(request.getNumeroFactura());
        compra.setEstatus(request.getEstatus());

        Map<String, CompraProductos> existentes = compra.getProductos().stream().collect(Collectors.toMap(cp -> cp.getInventario().getIdInventario(), Function.identity()));

        List<CompraProductos> listaProductos = compra.getProductos();
        
        for (CompraProductoRequestDTO dto : request.getProductos()) {
            Long idInventario = dto.getIn
        }

        if (!compra.getProductos().equals(request.getProductos())) {
            
            request.getProductos().forEach(e -> {
                if (!compra.getProductos().contains(e)) {    
                    Inventario inventario = inventarioService.obtenerOCrearInventario(e);
                    CompraProductos cp = new CompraProductos();

                    cp.setCompra(compra);
                    cp.setInventario(inventario);
                    cp.setCantidad(e.getCantidad());
                    cp.setCostoTotal(e.getCostoTotal());
                    cp.setSubtotal(e.getCostoUnitario() != null ? e.getCostoUnitario() : null);

                    listaProductos.add(cp);
                }
                
                //compraProductosRepository.save(cp);
            });
        }

        compra.setTotal(totalCompra);
        compra.setProductos(listaProductos);
        compraRepository.save(compra);
        listaProductos.forEach(lp -> compraProductosRepository.save(lp));

        return compraMapper.toResponse(compra);
    }

    private String generarKey(Inventario inv) {
        return inv.getProducto().getIdProducto() + "|" + inv.getLote() + "|" + inv.getFechaCaducidad();
    }
}