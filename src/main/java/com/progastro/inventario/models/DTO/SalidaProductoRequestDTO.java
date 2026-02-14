package com.progastro.inventario.models.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalidaProductoRequestDTO {
    @NotNull
    private Long idInventario;

    @NotNull
    private Integer cantidad;

    private BigDecimal subtotal;

    private BigDecimal total;
}
