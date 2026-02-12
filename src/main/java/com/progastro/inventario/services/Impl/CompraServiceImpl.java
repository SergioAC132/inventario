package com.progastro.inventario.services.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import static com.progastro.inventario.util.Constantes.COMPRA_NO_ENCONTRADA_ID;

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
        Proveedor proveedor = validarDatosProveedor(request.getProveedorId(), request.getNumeroFactura(), null);
        

        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setProveedor(proveedor);
        compra.setNumeroFactura(request.getNumeroFactura());
        compra.setEstatus(request.getEstatus() == null   ? EstatusCompra.REGISTRADA : request.getEstatus());

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

    private Proveedor validarDatosProveedor(Long proveedorId, String numeroFactura, Long idCompra) {

        Proveedor proveedor = proveedorRepository.findById(proveedorId).orElseThrow(() ->
            new ResourceNotFoundException(("Proveedor no encontrado con id" + proveedorId))
        );

        boolean facturaDuplicada;

        if (idCompra == null) {
            facturaDuplicada = compraRepository.existsByNumeroFacturaAndProveedor(numeroFactura, proveedor);
        } else {
            facturaDuplicada = compraRepository.existsByNumeroFacturaAndProveedorAndIdCompraNot(numeroFactura, proveedor, idCompra);    
        }

        if (facturaDuplicada) {
            throw new ValidationException(
                "Ya existe una compra con ese número de factura para este proveedor"
            );
        }

        return proveedor;
    }

    @Override
    @Transactional(readOnly= true)
    public Page<CompraResponseDTO> listarCompras(String proveedor, String estatus, LocalDate fechaInicio, LocalDate fechaFin, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());

        Page<Compra> compras = compraRepository.findByFiltros(proveedor, estatus, fechaInicio, fechaFin, pageable);

        compras.forEach(c -> c.setProductos(compraProductosRepository.findByCompra(c)));
        
        return compras.map(compraMapper::toResponse);
    }

    @Override
    @Transactional
    public CompraResponseDTO editarCompra (CompraRequestDTO request) {

        Proveedor proveedor = validarDatosProveedor(request.getProveedorId(), request.getNumeroFactura(), request.getIdCompra());
        

        Compra compra = compraRepository.findById(request.getIdCompra()).orElseThrow(() ->
            new ResourceNotFoundException((COMPRA_NO_ENCONTRADA_ID + request.getIdCompra()))
        );
        compra.setProveedor(proveedor);
        compra.setNumeroFactura(request.getNumeroFactura());
        compra.setEstatus(request.getEstatus() == null   ? EstatusCompra.REGISTRADA : request.getEstatus());

        Map<Long, CompraProductos> existentes = compra.getProductos().stream()
                                                                       .collect(Collectors.toMap(
                                                                        cp -> cp.getInventario()
                                                                        .getIdInventario(), Function.identity()));
        
        for (CompraProductoRequestDTO dto : request.getProductos()) {
            Inventario inventario = inventarioService.obtenerOCrearInventario(dto);
            Long key = inventario.getIdInventario();

            if (existentes.containsKey(key)) {
                CompraProductos cp = existentes.get(key);
                cp.setCantidad(dto.getCantidad());
                cp.setCostoTotal(dto.getCostoTotal());
                cp.setSubtotal(dto.getCostoUnitario());
            } else {
                CompraProductos nuevo = new CompraProductos();
                nuevo.setCompra(compra);
                nuevo.setInventario(inventario);
                nuevo.setCantidad(dto.getCantidad());
                nuevo.setCostoTotal(dto.getCostoTotal());
                nuevo.setSubtotal(dto.getCostoUnitario());

                compra.getProductos().add(nuevo);
            }
        }

        Set<String> keysRequest  = request.getProductos().stream().map(dto -> dto.getProductoId() + "|" + dto.getLote() + "|" + dto.getFechaCaducidad()).collect(Collectors.toSet());

        compra.getProductos().forEach(cp -> {
            if (!keysRequest.contains(generarKey(cp.getInventario()))) {
                inventarioService.revertirIngresoPorCompra(cp);
            }
        });

        compra.getProductos().removeIf(cp -> !keysRequest.contains(generarKey(cp.getInventario())));

        BigDecimal totalCompra = compra.getProductos().stream().map(CompraProductos::getCostoTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        compra.setTotal(totalCompra);
        compraRepository.save(compra);
        return compraMapper.toResponse(compra);
    }

    private String generarKey(Inventario inv) {
        return inv.getProducto().getIdProducto() + "|" + inv.getLote() + "|" + inv.getFechaCaducidad();
    }

    @Override
    @Transactional
    public void cancelarCompra(Long idCompra) {
        Compra compra = compraRepository.findById(idCompra).orElseThrow(() ->
            new ResourceNotFoundException((COMPRA_NO_ENCONTRADA_ID + idCompra))
        );

        if (compra.getEstatus() == EstatusCompra.CANCELADA) return;

        compra.setEstatus(EstatusCompra.CANCELADA);
        compra.getProductos().forEach(cp -> inventarioService.revertirIngresoPorCompra(cp));
        compraRepository.save(compra);
    }
}