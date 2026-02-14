package com.progastro.inventario.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalidaProductoResponseDTO {
    private Long idSalidaProducto;
    private Long idProducto;
    private String nombreProducto;
    private String marca;
    private String lote;
    private LocalDate fechaCaducidad;
    private Integer cantidad;
    private Integer cantidadRestante;
    private BigDecimal costoTotal;
}
