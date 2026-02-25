package com.progastro.inventario.services.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import com.progastro.inventario.services.LoginService;
import com.progastro.inventario.services.SalidaServiceBridge;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalidaServiceImpl implements SalidaServiceBridge {
    
    private final InventarioRepository inventarioRepository;
    private final InventarioServiceBridge inventarioService;
    private final LoginService loginService;
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

        loginService.settearUsuario();

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
    public Page<SalidaResponseDTO> listarSalidas(String nombreProducto, String destino, String tipo, 
                                                    LocalDate fechaInicio, LocalDate fechaFin, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        Page<Salida> salidas = salidaRepository.findByFiltros(nombreProducto, destino, tipo, fechaInicio, fechaFin, pageable);
        return salidas.map(salidaMapper::toResponse);
    }

    @Override
    @Transactional
    public SalidaResponseDTO editarSalida(SalidaRequestDTO request) {

        Salida salida = salidaRepository.findById(request.getIdSalida())
            .orElseThrow(() -> new ResourceNotFoundException("Salida no encontrada"));

        salida.setFecha(request.getFecha());
        salida.setTipo(request.getTipo());
        salida.setDestino(request.getDestino());

        validarEdicion(request.getProductos(), salida.getProductos());

        Map<Long, SalidaProductos> productosActuales = salida.getProductos().stream()
            .collect(Collectors.toMap(
                sp -> sp.getInventario().getIdInventario(),
                Function.identity()
            ));

        Map<Long, SalidaProductoRequestDTO> productosRequest = request.getProductos().stream()
            .collect(Collectors.toMap(
                SalidaProductoRequestDTO::getIdInventario,
                Function.identity()
            ));

        salida.getProductos().removeIf(sp -> {
            Long idInventario = sp.getInventario().getIdInventario();

            if (!productosRequest.containsKey(idInventario)) {
                // devolver stock
                Inventario inventario = sp.getInventario();
                inventario.setCantidadDisponible(
                    inventario.getCantidadDisponible() + sp.getCantidad()
                );
                return true; 
            }
            return false;
        });

        for (SalidaProductoRequestDTO dto : request.getProductos()) {

            Inventario inventario = inventarioRepository.findById(dto.getIdInventario())
                .orElseThrow(() ->
                    new ResourceNotFoundException("Inventario no encontrado")
                );

            if (productosActuales.containsKey(dto.getIdInventario())) {
                SalidaProductos existente = productosActuales.get(dto.getIdInventario());

                int cantidadOriginal = existente.getCantidad();
                int cantidadNueva = dto.getCantidad();
                int diferencia = cantidadNueva - cantidadOriginal;

                if (diferencia > 0 && diferencia > inventario.getCantidadDisponible()) {
                    throw new ValidationException(
                        "Stock insuficiente para el producto " + inventario.getIdInventario()
                    );
                }

                inventario.setCantidadDisponible(
                    inventario.getCantidadDisponible() - diferencia
                );

                existente.setCantidad(cantidadNueva);

                existente.setCostoTotal(dto.getTotal());
                existente.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal() : null);

            } else {
                if (dto.getCantidad() > inventario.getCantidadDisponible()) {
                    throw new ValidationException(
                        "Stock insuficiente para el producto " + inventario.getIdInventario()
                    );
                }

                inventario.setCantidadDisponible(
                    inventario.getCantidadDisponible() - dto.getCantidad()
                );

                SalidaProductos nuevo = new SalidaProductos();
                nuevo.setSalida(salida);
                nuevo.setInventario(inventario);
                nuevo.setCantidad(dto.getCantidad());
                nuevo.setCostoTotal(dto.getTotal());

                salida.getProductos().add(nuevo);
            }
        }

        BigDecimal totalSalida = salida.getProductos().stream()
        .map(SalidaProductos::getCostoTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

        loginService.settearUsuario();

        salida.setTotal(totalSalida);

        salidaRepository.save(salida);
        return salidaMapper.toResponse(salida);
    }
}
