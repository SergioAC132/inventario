package com.progastro.inventario.services;

import org.springframework.data.domain.Page;

import com.progastro.inventario.models.DTO.UsuarioRequestDTO;
import com.progastro.inventario.models.DTO.UsuarioResponseDTO;

public interface UsuarioServiceBridge {
    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO request);
    UsuarioResponseDTO editarUsuario(UsuarioRequestDTO request);
    void cambiarEstadoUsuario(Long idUsuario);
    Page<UsuarioResponseDTO> listarUsuarios(String username, String rol, Boolean enabled, int page, int size);
    UsuarioResponseDTO me(String username);
}