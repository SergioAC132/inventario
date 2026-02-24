package com.progastro.inventario.mappers;

import org.springframework.stereotype.Component;

import com.progastro.inventario.models.DTO.ProveedorResponseDTO;
import com.progastro.inventario.models.Entities.Proveedor;

@Component
public class ProveedorMapper {
    public ProveedorResponseDTO toResponse(Proveedor proveedor) {
        return new ProveedorResponseDTO(
            proveedor.getIdProveedor(),
            proveedor.getNombre(),
            proveedor.getRfc(),
            proveedor.getTipoPersona(),
            proveedor.getTelefono(),
            proveedor.getCorreo(),
            proveedor.getCodigoPostal()
        );
    }
}