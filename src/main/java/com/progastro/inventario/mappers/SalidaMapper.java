package com.progastro.inventario.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.SalidaProductoResponseDTO;
import com.progastro.inventario.models.DTO.SalidaResponseDTO;
import com.progastro.inventario.models.Entities.Salida;
import com.progastro.inventario.models.Entities.SalidaProductos;

@Component
public class SalidaMapper {
    
    public SalidaResponseDTO toResponse(Salida salida) {
        List<SalidaProductoResponseDTO> productos = salida.getProductos()
                                                        .stream()
                                                        .map(this::mapProducto)
                                                        .toList();

        return new SalidaResponseDTO(
            salida.getIdSalida(),
            salida.getFecha(),
            salida.getTipo().toString(),
            salida.getDestino().toString(),
            salida.getTotal(),
            productos
        );
    }

    private SalidaProductoResponseDTO mapProducto(SalidaProductos sp) {
        return new SalidaProductoResponseDTO(
            sp.getIdSalidaProducto(),
            sp.getInventario().getIdInventario(),
            sp.getInventario().getProducto().getIdProducto(),
            sp.getInventario().getProducto().getNombre(),
            sp.getInventario().getProducto().getMarca().getNombre(),
            sp.getInventario().getLote(),
            sp.getInventario().getFechaCaducidad(),
            sp.getCantidad(),
            sp.getInventario().getCantidadDisponible(),
            sp.getCostoTotal()
        );
    }
}
