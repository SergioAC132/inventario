package com.progastro.inventario.services.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.exceptions.ValidationException;
import com.progastro.inventario.mappers.ProveedorMapper;
import com.progastro.inventario.models.DTO.ProveedorRequestDTO;
import com.progastro.inventario.models.DTO.ProveedorResponseDTO;
import com.progastro.inventario.models.Entities.Proveedor;
import com.progastro.inventario.repositories.ProveedorRepository;
import com.progastro.inventario.services.ProveedorServiceBridge;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorServiceBridge {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    @Transactional
    public ProveedorResponseDTO registrarProveedor(ProveedorRequestDTO request) {
        if (proveedorRepository.existsByRfcIgnoreCase(request.getRfc())) {
            throw new ValidationException("Ya existe un proveedor con el RFC: " + request.getRfc());
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(request.getNombre());
        proveedor.setRfc(request.getRfc().toUpperCase());
        proveedor.setTipoPersona(request.getTipoPersona());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setCorreo(request.getCorreo());
        proveedor.setCodigoPostal(request.getCodigoPostal());

        return proveedorMapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    public ProveedorResponseDTO editarProveedor(ProveedorRequestDTO request) {
        if (proveedorRepository.existsByRfcIgnoreCaseAndIdProveedorNot(request.getRfc(), request.getIdProveedor())) {
            throw new ValidationException("Ya existe un proveedor con el RFC: " + request.getRfc());
        }

        Proveedor proveedor = proveedorRepository.findById(request.getIdProveedor()).orElseThrow(() ->
            new ResourceNotFoundException("Proveedor no encontrado con id: " + request.getIdProveedor())
        );

        proveedor.setNombre(request.getNombre());
        proveedor.setRfc(request.getRfc().toUpperCase());
        proveedor.setTipoPersona(request.getTipoPersona());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setCorreo(request.getCorreo());
        proveedor.setCodigoPostal(request.getCodigoPostal());

        return proveedorMapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProveedorResponseDTO> listarProveedores(String nombre, String rfc, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return proveedorRepository.findByFiltros(nombre, rfc, pageable)
                .map(proveedorMapper::toResponse);
    }
}