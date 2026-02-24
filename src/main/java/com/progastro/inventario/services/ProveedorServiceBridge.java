package com.progastro.inventario.services;

import org.springframework.data.domain.Page;

import com.progastro.inventario.models.DTO.ProveedorRequestDTO;
import com.progastro.inventario.models.DTO.ProveedorResponseDTO;

public interface ProveedorServiceBridge {
    ProveedorResponseDTO registrarProveedor(ProveedorRequestDTO request);
    ProveedorResponseDTO editarProveedor(ProveedorRequestDTO request);
    Page<ProveedorResponseDTO> listarProveedores(String nombre, String rfc, int page, int size);
}