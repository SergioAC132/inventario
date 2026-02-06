package com.progastro.inventario.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.CompraProductoReponseDTO;
import com.progastro.inventario.models.DTO.CompraResponseDTO;
import com.progastro.inventario.models.Entities.Compra;
import com.progastro.inventario.models.Entities.CompraProductos;

@Component
public class CompraMapper {
    
    public CompraResponseDTO toResponse(Compra compra) {

        List<CompraProductoReponseDTO> productos = compra.getProductos()
                                                        .stream()
                                                        .map(this::mapProducto)
                                                        .toList();

        return new CompraResponseDTO(
            compra.getIdCompra(),
            compra.getFecha(),
            compra.getProveedor().getNombre(),
            compra.getNumeroFactura(),
            compra.getEstatus(),
            compra.getTotal(),
            productos
        );
    }

    private CompraProductoReponseDTO mapProducto(CompraProductos cp) {
        return new CompraProductoReponseDTO(

            cp.getIdCompraProducto(),
            cp.getInventario().getProducto().getIdProducto(),
            cp.getInventario().getProducto().getNombre(),
            cp.getInventario().getProducto().getMarca().getNombre(),
            cp.getInventario().getLote(),
            cp.getInventario().getFechaCaducidad(),
            cp.getInventario().getCantidadDisponible(),
            cp.getCostoTotal()
        );

    }
}