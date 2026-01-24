package com.progastro.inventario.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompraProductoReponseDTO {
    
    private Long idProducto;
    private String nombreProducto;
    private String marca;
    private String lote;
    private String fechaCaducidad;
    private String cantidad;
    private String costoTotal;
}
