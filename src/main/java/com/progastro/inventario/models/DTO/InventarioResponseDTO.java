package com.progastro.inventario.models.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InventarioResponseDTO {
    private Long idInventario;
    private String lote;
    private Integer cantidadDisponible;
    private LocalDate fechaCaducidad;
    private Boolean active;
}
