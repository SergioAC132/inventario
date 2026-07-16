package com.progastro.inventario.services.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progastro.inventario.mappers.DoctorMapper;
import com.progastro.inventario.models.DTO.DoctorRequestDTO;
import com.progastro.inventario.models.DTO.DoctorResponseDTO;
import com.progastro.inventario.models.Entities.Doctor;
import com.progastro.inventario.repositories.DoctorRepository;
import com.progastro.inventario.services.DoctorServiceBridge;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorServiceBridge {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    @Transactional
    public DoctorResponseDTO registrarDoctor(DoctorRequestDTO request) {
        Doctor doctor = new Doctor();
        doctor.setNombre(capitalizarPalabras(request.getNombre()));
        doctor.setTelefono(request.getTelefono());

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    private String capitalizarPalabras(String texto) {
        String normalizado = texto.trim().replaceAll("\\s+", " ").toLowerCase();
        StringBuilder resultado = new StringBuilder(normalizado.length());
        boolean inicioPalabra = true;
        for (char c : normalizado.toCharArray()) {
            if (Character.isWhitespace(c)) {
                inicioPalabra = true;
                resultado.append(c);
            } else if (inicioPalabra) {
                resultado.append(Character.toUpperCase(c));
                inicioPalabra = false;
            } else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponseDTO> listarDoctores(String nombre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return doctorRepository.findByFiltros(nombre, pageable)
                .map(doctorMapper::toResponse);
    }
}
