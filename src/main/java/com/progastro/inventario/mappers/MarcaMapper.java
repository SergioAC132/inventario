package com.progastro.inventario.mappers;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.MarcaResponseDTO;
import com.progastro.inventario.models.Entities.Marca;

@Component
public class MarcaMapper {
    public MarcaResponseDTO toResponse(Marca marca) {
        return new MarcaResponseDTO(
            marca.getIdMarca(),
            marca.getNombre()
        );
    }
}
