package com.progastro.inventario.mappers;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.DoctorResponseDTO;
import com.progastro.inventario.models.Entities.Doctor;

@Component
public class DoctorMapper {
    public DoctorResponseDTO toResponse(Doctor doctor) {
        return new DoctorResponseDTO(
            doctor.getIdDoctor(),
            doctor.getNombre(),
            doctor.getTelefono()
        );
    }
}
