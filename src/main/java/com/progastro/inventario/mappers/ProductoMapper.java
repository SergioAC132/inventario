package com.progastro.inventario.mappers;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.ProductoResponseDTO;
import com.progastro.inventario.models.Entities.Producto;

@Component
public class ProductoMapper {
    
    public ProductoResponseDTO toResponse(Producto producto) {
        return new ProductoResponseDTO(
            producto.getIdProducto(),
            producto.getCodigo(),
            producto.getNombre(),
            producto.getMarca().getNombre()
        );
    }
}
