package com.progastro.inventario.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarcaResponseDTO {
    private Long idMarca;
    
    private String nombre;
}
