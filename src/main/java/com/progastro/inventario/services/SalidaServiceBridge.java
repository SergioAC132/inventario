package com.progastro.inventario.services;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.progastro.inventario.models.DTO.SalidaRequestDTO;
import com.progastro.inventario.models.DTO.SalidaResponseDTO;

public interface SalidaServiceBridge {
    SalidaResponseDTO registrarSalida(SalidaRequestDTO request);

    Page<SalidaResponseDTO> listarSalidas(String producto, String destino, String tipo, LocalDate fechaInicio, LocalDate fechaFin,
                                          Long folio, String destinatario, int page, int size);

    SalidaResponseDTO editarSalida(SalidaRequestDTO request);

    SalidaResponseDTO devolverProducto(Long idSalidaProducto);

    void eliminarSalida(Long idSalida, String password);
}
