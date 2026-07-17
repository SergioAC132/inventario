package com.progastro.inventario.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.NotaResponseDTO;
import com.progastro.inventario.models.Entities.Nota;

@Component
public class NotaMapper {

    public List<NotaResponseDTO> toResponseList(List<Nota> notas) {
        return notas.stream().map(this::toResponse).toList();
    }

    public NotaResponseDTO toResponse(Nota nota) {
        return new NotaResponseDTO(
            nota.getIdNota(),
            nota.getFecha(),
            nota.getTexto(),
            nota.getUsuario().getUsername()
        );
    }
}
