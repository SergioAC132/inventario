package com.progastro.inventario.models.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotaResponseDTO {
    private Long idNota;
    private Date fecha;
    private String texto;
    private String usuario;
}
