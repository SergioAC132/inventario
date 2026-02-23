package com.progastro.inventario.mappers;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.UsuarioResponseDTO;
import com.progastro.inventario.models.Entities.Usuario;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getIdUsuario(),
            usuario.getUsername(),
            usuario.getRol().getNombre(),
            usuario.getEnabled()
        );
    }
}