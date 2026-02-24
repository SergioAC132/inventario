package com.progastro.inventario.models.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {
    
    private Long idProducto;

    private String codigo;

    private String nombre;

    private String nombreMarca;

    private Integer stockTotal;

    private List<InventarioResponseDTO> inventarios;
}
