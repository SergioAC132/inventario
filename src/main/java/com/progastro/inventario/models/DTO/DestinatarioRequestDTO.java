package com.progastro.inventario.models.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DestinatarioRequestDTO {

    private Long idDestinatario;

    @NotBlank
    private String nombre;
}
