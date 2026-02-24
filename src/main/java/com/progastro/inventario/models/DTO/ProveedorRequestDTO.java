package com.progastro.inventario.models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorRequestDTO {

    private Long idProveedor;

    @NotBlank
    private String nombre;

    @NotBlank
    @Size(min = 12, max = 13)
    private String rfc;

    @NotNull
    private Byte tipoPersona;

    @NotBlank
    private String telefono;

    private String correo;

    private Integer codigoPostal;
}