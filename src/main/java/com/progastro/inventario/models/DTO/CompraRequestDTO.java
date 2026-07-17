package com.progastro.inventario.models.DTO;

import java.time.LocalDate;
import java.util.List;

import com.progastro.inventario.models.Enums.EstatusCompra;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompraRequestDTO {
    
    private Long idCompra;

    private Long proveedorId;

    private Long doctorId;

    private LocalDate fecha;

    private String numeroFactura;

    @NotEmpty
    private List<CompraProductoRequestDTO> productos;

    private EstatusCompra estatus = EstatusCompra.REGISTRADA;

    private String nota;
}
