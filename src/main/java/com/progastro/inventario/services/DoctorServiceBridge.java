package com.progastro.inventario.services;

import org.springframework.data.domain.Page;

import com.progastro.inventario.models.DTO.DoctorRequestDTO;
import com.progastro.inventario.models.DTO.DoctorResponseDTO;

public interface DoctorServiceBridge {
    DoctorResponseDTO registrarDoctor(DoctorRequestDTO request);
    Page<DoctorResponseDTO> listarDoctores(String nombre, int page, int size);
}
