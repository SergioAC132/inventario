package com.progastro.inventario.models.DTO;

import java.util.List;

import com.progastro.inventario.models.Enums.EstatusCompra;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private Long proveedorId;

    @NotBlank
    private String numeroFactura;

    @NotEmpty
    private List<CompraProductoRequestDTO> productos;

    private EstatusCompra estatus = EstatusCompra.REGISTRADA;
}
