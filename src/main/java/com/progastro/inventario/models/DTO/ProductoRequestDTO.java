package com.progastro.inventario.models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequestDTO {

    private Long idProducto;

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombre;
    
    @NotNull
    private Long idMarca;
}
