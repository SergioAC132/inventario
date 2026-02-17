package com.progastro.inventario.services.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.exceptions.ValidationException;
import com.progastro.inventario.mappers.SalidaMapper;
import com.progastro.inventario.models.DTO.SalidaProductoRequestDTO;
import com.progastro.inventario.models.DTO.SalidaRequestDTO;
import com.progastro.inventario.models.DTO.SalidaResponseDTO;
import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Salida;
import com.progastro.inventario.models.Entities.SalidaProductos;
import com.progastro.inventario.repositories.InventarioRepository;
import com.progastro.inventario.repositories.SalidaProductosRepository;
import com.progastro.inventario.repositories.SalidaRepository;
import com.progastro.inventario.services.InventarioServiceBridge;
import com.progastro.inventario.services.SalidaServiceBridge;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalidaServiceImpl implements SalidaServiceBridge {
    
    private final InventarioRepository inventarioRepository;
    private final InventarioServiceBridge inventarioService;
    private final SalidaMapper salidaMapper;
    private final SalidaRepository salidaRepository;
    private final SalidaProductosRepository salidaProductosRepository;

    @Override
    @Transactional
    public SalidaResponseDTO registrarSalida(SalidaRequestDTO request) {
        List<SalidaProductos> listaProductos = new ArrayList<>();
    
        validarCreacion(request.getProductos());
        
        Salida salida = new Salida();
        salida.setFecha(request.getFecha());
        salida.setTipo(request.getTipo());
        salida.setDestino(request.getDestino());

        BigDecimal totalSalida = request.getProductos()
                    .stream()
                    .map(SalidaProductoRequestDTO::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        request.getProductos().forEach(p -> {
            Inventario inventario = inventarioService.restarStock(p);

            SalidaProductos sp = new SalidaProductos();

            sp.setSalida(salida);
            sp.setInventario(inventario);
            sp.setCantidad(p.getCantidad());
            sp.setCostoTotal(p.getTotal());
            sp.setSubtotal(p.getSubtotal() != null ? p.getSubtotal() : null);

            listaProductos.add(sp);
        });

        salida.setTotal(totalSalida);
        salida.setProductos(listaProductos);
        salidaRepository.save(salida);
        listaProductos.forEach(lp -> salidaProductosRepository.save(lp));

        return salidaMapper.toResponse(salida);
    }

    private void validarCreacion(List<SalidaProductoRequestDTO> productos) {
        productos.forEach(pr -> {
            Inventario inventario = inventarioRepository.findById(pr.getIdInventario()).orElseThrow(() ->
                new ResourceNotFoundException("Inventario no encontrado con id " + pr.getIdInventario())
            );

            if (pr.getCantidad() > inventario.getCantidadDisponible()) {
                throw new ValidationException("Inventario con stock insuficiente");
            }
        });
    }

    private void validarEdicion(
            List<SalidaProductoRequestDTO> productosRequest,
            List<SalidaProductos> productosOriginales) {

        for (SalidaProductoRequestDTO pr : productosRequest) {

            Inventario inventario = inventarioRepository.findById(pr.getIdInventario())
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Inventario no encontrado con id " + pr.getIdInventario()
                    )
                );

            // Buscar cuánto tenía originalmente ese producto en la salida
            int cantidadOriginal = productosOriginales.stream()
                .filter(po -> po.getInventario().getIdInventario()
                    .equals(pr.getIdInventario()))
                .map(SalidaProductos::getCantidad)
                .findFirst()
                .orElse(0);

            int diferencia = pr.getCantidad() - cantidadOriginal;

            // Sólo validar si está aumentando
            if (diferencia > 0 && diferencia > inventario.getCantidadDisponible()) {
                throw new ValidationException(
                    "Inventario con stock insuficiente. Disponible: "
                    + inventario.getCantidadDisponible()
                );
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Page<SalidaResponseDTO> listarSalidas(String nombreProducto, String destino, String tipo, LocalDate fechaInicio, LocalDate fechaFin, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        Page<Salida> salidas = salidaRepository.findByFiltros(nombreProducto, destino, tipo, fechaInicio, fechaFin, pageable);
        return salidas.map(salidaMapper::toResponse);
    }

    @Override
    @Transactional
    public SalidaResponseDTO editarSalida(SalidaRequestDTO request) {

        Salida salida = salidaRepository.findById(request.getIdSalida()).orElseThrow(() ->
            new ResourceNotFoundException(("Salida no encontrada con el id: " + request.getIdSalida())));

        salida.setDestino(request.getDestino());
        salida.setTipo(request.getTipo());
        salida.setFecha(request.getFecha());

        validarEdicion(request.getProductos(), salida.getProductos());
        
        BigDecimal totalSalida = request.getProductos()
                    .stream()
                    .map(SalidaProductoRequestDTO::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, SalidaProductos> existentes = salida.getProductos().stream()
                                                                    .collect(Collectors.toMap(
                                                                        sp -> sp.getInventario().getIdInventario(),
                                                                        Function.identity()));

        for (SalidaProductoRequestDTO dto : request.getProductos()) {
            Inventario inventario = inventarioService.obtenerOCrearInventario(dto);
            
        }
    }

}
