package com.progastro.inventario.services;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.progastro.inventario.models.DTO.CompraRequestDTO;
import com.progastro.inventario.models.DTO.CompraResponseDTO;

public interface CompraServiceBridge {
    CompraResponseDTO registrarCompra(CompraRequestDTO request);

    CompraResponseDTO editarCompra (Long idCompra, CompraRequestDTO request);

    Page<CompraResponseDTO> listarCompras(String proveedor, String estatus, LocalDate fechaInicio, LocalDate fechaFin, int page, int size);
}
