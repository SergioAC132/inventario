package com.progastro.inventario.models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {

    private Long idUsuario;

    @NotBlank
    private String username;

    private String password;

    @NotNull
    private Long idRol;
}