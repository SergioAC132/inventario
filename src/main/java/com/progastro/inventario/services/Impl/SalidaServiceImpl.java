package com.progastro.inventario.services.Impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
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
    
        validarInventarioProductos(request.getProductos());
        
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

    private void validarInventarioProductos(List<SalidaProductoRequestDTO> productos) {
        productos.forEach(pr -> {
            Inventario inventario = inventarioRepository.findById(pr.getIdInventario()).orElseThrow(() ->
                new ResourceNotFoundException("Inventario no encontrado con id " + pr.getIdInventario())
            );

            if (pr.getCantidad() > inventario.getCantidadDisponible()) {
                throw new ValidationException("Inventario con stock insuficiente");
            }
        });
    }
}
