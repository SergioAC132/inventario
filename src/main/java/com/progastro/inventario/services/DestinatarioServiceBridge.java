package com.progastro.inventario.services;

import org.springframework.data.domain.Page;

import com.progastro.inventario.models.DTO.DestinatarioRequestDTO;
import com.progastro.inventario.models.DTO.DestinatarioResponseDTO;

public interface DestinatarioServiceBridge {
    DestinatarioResponseDTO registrarDestinatario(DestinatarioRequestDTO request);
    DestinatarioResponseDTO editarDestinatario(DestinatarioRequestDTO request);
    Page<DestinatarioResponseDTO> listarDestinatarios(String nombre, int page, int size);
}
