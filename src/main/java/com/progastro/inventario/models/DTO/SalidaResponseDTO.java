package com.progastro.inventario.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalidaResponseDTO {
    private Long idSalida;
    private LocalDate fecha;
    private String tipo;
    private String destino;
    private Long folio;
    private String destinatario;
    private BigDecimal total;
    private List<SalidaProductoResponseDTO> productos;
    private List<NotaResponseDTO> notas;
}
