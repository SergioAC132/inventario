package com.progastro.inventario.services;

import org.springframework.data.domain.Page;

import com.progastro.inventario.models.DTO.MarcaRequestDTO;
import com.progastro.inventario.models.DTO.MarcaResponseDTO;

public interface MarcaServiceBridge {
    MarcaResponseDTO registrarMarca(MarcaRequestDTO request);

    Page<MarcaResponseDTO> listarMarcas(String nombre, int page, int size);

    MarcaResponseDTO editarMarca(MarcaRequestDTO request);
}
