package com.progastro.inventario.services;

import com.progastro.inventario.models.DTO.SalidaRequestDTO;
import com.progastro.inventario.models.DTO.SalidaResponseDTO;

public interface SalidaServiceBridge {
    SalidaResponseDTO registrarSalida(SalidaRequestDTO request);
}
