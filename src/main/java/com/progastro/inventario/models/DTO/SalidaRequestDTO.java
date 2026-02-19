package com.progastro.inventario.models.DTO;

import java.time.LocalDate;
import java.util.List;

import com.progastro.inventario.models.Enums.DestinoSalida;
import com.progastro.inventario.models.Enums.TipoSalida;

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
public class SalidaRequestDTO {
    
    private Long idSalida;

    @NotNull
    private LocalDate fecha;
    private TipoSalida tipo;
    private DestinoSalida destino;

    @NotEmpty
    private List<SalidaProductoRequestDTO> productos;
}
