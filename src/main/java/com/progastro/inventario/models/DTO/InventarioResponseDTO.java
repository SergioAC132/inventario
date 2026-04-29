package com.progastro.inventario.models.DTO;

import java.math.BigDecimal;
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
    private BigDecimal costoUnitario;
    private Boolean active;
}
