package com.progastro.inventario.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;
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
public class CompraProductoRequestDTO {
    
    @NotNull
    private Long productoId;

    @NotBlank
    private String lote;

    @NotNull
    private LocalDate fechaCaducidad;

    @NotNull
    @Min(1)
    private Integer cantidad;

    private BigDecimal costoUnitario;

    @NotNull
    private BigDecimal costoTotal;
    
}
