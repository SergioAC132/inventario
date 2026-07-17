package com.progastro.inventario.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.CompraProductoReponseDTO;
import com.progastro.inventario.models.DTO.CompraResponseDTO;
import com.progastro.inventario.models.DTO.NotaResponseDTO;
import com.progastro.inventario.models.Entities.Compra;
import com.progastro.inventario.models.Entities.CompraProductos;
import com.progastro.inventario.models.Entities.Nota;

@Component
public class CompraMapper {

    private final NotaMapper notaMapper;

    public CompraMapper(NotaMapper notaMapper) {
        this.notaMapper = notaMapper;
    }

    public CompraResponseDTO toResponse(Compra compra, List<Nota> notas) {

        List<CompraProductoReponseDTO> productos = compra.getProductos()
                                                        .stream()
                                                        .map(this::mapProducto)
                                                        .toList();

        List<NotaResponseDTO> notasResponse = notas != null ? notaMapper.toResponseList(notas) : List.of();

        return new CompraResponseDTO(
            compra.getIdCompra(),
            compra.getFecha(),
            compra.getProveedor() != null ? compra.getProveedor().getNombre() : null,
            compra.getDoctor() != null ? compra.getDoctor().getNombre() : null,
            compra.getNumeroFactura(),
            compra.getEstatus(),
            compra.getTotal(),
            productos,
            notasResponse
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
            cp.getCantidad(),
            cp.getCostoTotal()
        );

    }
}