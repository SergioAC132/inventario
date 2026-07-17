package com.progastro.inventario.services.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
import com.progastro.inventario.models.Entities.Doctor;
import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Nota;
import com.progastro.inventario.models.Entities.Proveedor;
import com.progastro.inventario.models.Enums.EstatusCompra;
import com.progastro.inventario.repositories.CompraProductosRepository;
import com.progastro.inventario.repositories.CompraRepository;
import com.progastro.inventario.repositories.DoctorRepository;
import com.progastro.inventario.repositories.NotaRepository;
import com.progastro.inventario.repositories.ProveedorRepository;
import com.progastro.inventario.services.CompraServiceBridge;
import com.progastro.inventario.services.InventarioServiceBridge;
import com.progastro.inventario.services.LoginService;
import static com.progastro.inventario.util.Constantes.COMPRA_NO_ENCONTRADA_ID;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraServiceBridge {

    private final CompraProductosRepository compraProductosRepository;
    private final CompraRepository compraRepository;
    private final DoctorRepository doctorRepository;
    private final LoginService loginService;
    private final NotaRepository notaRepository;
    private final ProveedorRepository proveedorRepository;
    private final InventarioServiceBridge inventarioService;
    private final CompraMapper compraMapper;

    private void registrarNotaSiAplica(Compra compra, String texto) {
        if (texto == null || texto.isBlank()) return;

        Nota nota = new Nota();
        nota.setCompra(compra);
        nota.setFecha(new Date());
        nota.setTexto(texto);
        nota.setUsuario(loginService.getUsuarioActual());
        notaRepository.save(nota);
    }

    @Override
    @Transactional
    public CompraResponseDTO registrarCompra(CompraRequestDTO request) {

        List<CompraProductos> listaProductos = new ArrayList<>();
        Proveedor proveedor = null;
        Doctor doctor = null;

        if (request.getProveedorId() != null) {
            if (request.getNumeroFactura() == null || request.getNumeroFactura().isBlank()) {
                throw new ValidationException("El número de factura es obligatorio");
            }
            proveedor = validarDatosProveedor(request.getProveedorId(), request.getNumeroFactura(), null);
        } else if (request.getDoctorId() != null) {
            doctor = validarDatosDoctor(request.getDoctorId());
        } else {
            throw new ValidationException("Debe indicar un proveedor o un doctor");
        }

        Compra compra = new Compra();
        compra.setFecha(request.getFecha());
        compra.setProveedor(proveedor);
        compra.setDoctor(doctor);
        compra.setNumeroFactura(request.getNumeroFactura());
        compra.setEstatus(request.getEstatus() == null   ? EstatusCompra.REGISTRADA : request.getEstatus());

        BigDecimal totalCompra = doctor != null
                ? BigDecimal.ZERO
                : request.getProductos()
                    .stream()
                    .map(CompraProductoRequestDTO::getCostoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        request.getProductos().forEach(e -> {
            Inventario inventario = inventarioService.obtenerOCrearInventario(e);

            CompraProductos cp = new CompraProductos();

            cp.setCompra(compra);
            cp.setInventario(inventario);
            cp.setCantidad(e.getCantidad());
            cp.setSubtotal(e.getSubtotal());
            cp.setCostoTotal(e.getCostoTotal());

            listaProductos.add(cp);
            //compraProductosRepository.save(cp);
        });

        loginService.settearUsuario();

        compra.setTotal(totalCompra);
        compra.setProductos(listaProductos);
        compraRepository.save(compra);
        listaProductos.forEach(lp -> compraProductosRepository.save(lp));
        registrarNotaSiAplica(compra, request.getNota());

        return compraMapper.toResponse(compra, notaRepository.findByCompraOrderByFechaAsc(compra));
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

    private Doctor validarDatosDoctor(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() ->
            new ResourceNotFoundException(("Doctor no encontrado con id" + doctorId))
        );

        return doctor;
    }

    @Override
    @Transactional(readOnly= true)
    public Page<CompraResponseDTO> listarCompras(String proveedor, String estatus, LocalDate fechaInicio, LocalDate fechaFin, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());

        Page<Compra> compras = compraRepository.findByFiltros(proveedor, estatus, fechaInicio, fechaFin, pageable);

        compras.forEach(c -> c.setProductos(compraProductosRepository.findByCompra(c)));

        return compras.map(c -> compraMapper.toResponse(c, notaRepository.findByCompraOrderByFechaAsc(c)));
    }

    @Override
    @Transactional
    public CompraResponseDTO editarCompra (CompraRequestDTO request) {

        Proveedor proveedor = null;
        Doctor doctor = null;

        if (request.getProveedorId() != null) {
            if (request.getNumeroFactura() == null || request.getNumeroFactura().isBlank()) {
                throw new ValidationException("El número de factura es obligatorio");
            }
            proveedor = validarDatosProveedor(request.getProveedorId(), request.getNumeroFactura(), request.getIdCompra());
        } else if (request.getDoctorId() != null) {
            doctor = validarDatosDoctor(request.getDoctorId());
        } else {
            throw new ValidationException("Debe indicar un proveedor o un doctor");
        }

        Compra compra = compraRepository.findById(request.getIdCompra()).orElseThrow(() ->
            new ResourceNotFoundException((COMPRA_NO_ENCONTRADA_ID + request.getIdCompra()))
        );
        compra.setProveedor(proveedor);
        compra.setDoctor(doctor);
        compra.setNumeroFactura(request.getNumeroFactura());
        compra.setEstatus(request.getEstatus() == null   ? EstatusCompra.REGISTRADA : request.getEstatus());

        Map<String, CompraProductos> existentesPorKey = compra.getProductos().stream()
                .collect(Collectors.toMap(
                    cp -> generarKey(cp.getInventario()), Function.identity()));

        for (CompraProductoRequestDTO dto : request.getProductos()) {
            String dtoKey = dto.getProductoId() + "|" + dto.getLote() + "|" + dto.getFechaCaducidad();

            if (existentesPorKey.containsKey(dtoKey)) {
                CompraProductos cp = existentesPorKey.get(dtoKey);
                int diferencia = dto.getCantidad() - cp.getCantidad();
                if (diferencia != 0) {
                    inventarioService.ajustarStockPorEdicionCompra(cp.getInventario(), diferencia);
                    inventarioService.ajustarCostoUnitarioPorEdicionCompra(cp.getInventario(), dto.getCostoTotal(), dto.getCantidad());
                }
                cp.setCantidad(dto.getCantidad());
                cp.setCostoTotal(dto.getCostoTotal());
                cp.setSubtotal(dto.getSubtotal());
            } else {
                Inventario inventario = inventarioService.obtenerOCrearInventario(dto);
                CompraProductos nuevo = new CompraProductos();
                nuevo.setCompra(compra);
                nuevo.setInventario(inventario);
                nuevo.setCantidad(dto.getCantidad());
                nuevo.setCostoTotal(dto.getCostoTotal());
                nuevo.setSubtotal(dto.getSubtotal());
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

        BigDecimal totalCompra = doctor != null
                ? BigDecimal.ZERO
                : compra.getProductos().stream().map(CompraProductos::getCostoTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        loginService.settearUsuario();

        compra.setTotal(totalCompra);
        compraRepository.save(compra);
        registrarNotaSiAplica(compra, request.getNota());
        return compraMapper.toResponse(compra, notaRepository.findByCompraOrderByFechaAsc(compra));
    }

    private String generarKey(Inventario inv) {
        return inv.getProducto().getIdProducto() + "|" + inv.getLote() + "|" + inv.getFechaCaducidad();
    }

    @Override
    @Transactional
    public void cancelarCompra(Long idCompra) {
        Compra compra = compraRepository.findByIdWithProductos(idCompra).orElseThrow(() ->
            new ResourceNotFoundException((COMPRA_NO_ENCONTRADA_ID + idCompra))
        );

        if (compra.getEstatus() == EstatusCompra.CANCELADA) return;

        loginService.settearUsuario();

        compra.setEstatus(EstatusCompra.CANCELADA);
        compra.getProductos().forEach(cp -> inventarioService.revertirIngresoPorCompra(cp));
        compraRepository.save(compra);
    }
}