package com.progastro.inventario.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompraResponseDTO {
    private Long idCompra;
    private LocalDateTime fecha;
    private String proveedor;
    private String numeroFactura;
    private String estatus;
    private BigDecimal total;
    private List<CompraProductoReponseDTO> productos;
}
