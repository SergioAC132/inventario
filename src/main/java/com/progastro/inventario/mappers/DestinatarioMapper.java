package com.progastro.inventario.mappers;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.DestinatarioResponseDTO;
import com.progastro.inventario.models.Entities.Destinatario;

@Component
public class DestinatarioMapper {
    public DestinatarioResponseDTO toResponse(Destinatario destinatario) {
        return new DestinatarioResponseDTO(
            destinatario.getIdDestinatario(),
            destinatario.getNombre()
        );
    }
}
