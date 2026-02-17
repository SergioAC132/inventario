package com.progastro.inventario.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.InventarioResponseDTO;
import com.progastro.inventario.models.DTO.ProductoResponseDTO;
import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Producto;

@Component
public class ProductoMapper {
    
    public ProductoResponseDTO toResponse(Producto producto) {

        List<InventarioResponseDTO> inventarios = producto.getInventarios()
                                                        .stream()
                                                        .map(this::mapInventario)
                                                        .toList(); 

        return new ProductoResponseDTO(
            producto.getIdProducto(),
            producto.getCodigo(),
            producto.getNombre(),
            producto.getMarca().getNombre(),
            inventarios
        );
    }

    private InventarioResponseDTO mapInventario(Inventario inventario) {
        return new InventarioResponseDTO(
            inventario.getIdInventario(),
            inventario.getLote(),
            inventario.getCantidadDisponible(),
            inventario.getFechaCaducidad(),
            inventario.getActive()
        );
    }
}
