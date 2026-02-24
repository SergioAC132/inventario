package com.progastro.inventario.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorResponseDTO {
    private Long idProveedor;
    private String nombre;
    private String rfc;
    private Byte tipoPersona;
    private String telefono;
    private String correo;
    private Integer codigoPostal;
}