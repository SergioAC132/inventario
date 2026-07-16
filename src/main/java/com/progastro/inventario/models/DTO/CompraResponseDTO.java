package com.progastro.inventario.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.progastro.inventario.models.Enums.EstatusCompra;

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
    private LocalDate fecha;
    private String proveedor;
    private String doctor;
    private String numeroFactura;
    private EstatusCompra estatus;
    private BigDecimal total;
    private List<CompraProductoReponseDTO> productos;
}
