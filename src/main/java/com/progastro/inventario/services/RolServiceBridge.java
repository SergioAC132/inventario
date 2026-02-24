package com.progastro.inventario.services;

import java.util.List;

import com.progastro.inventario.models.DTO.RolResponseDTO;

public interface RolServiceBridge {
    List<RolResponseDTO> listarRoles();
}
