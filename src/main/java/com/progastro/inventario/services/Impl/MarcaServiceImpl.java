package com.progastro.inventario.services.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.mappers.MarcaMapper;
import com.progastro.inventario.models.DTO.MarcaRequestDTO;
import com.progastro.inventario.models.DTO.MarcaResponseDTO;
import com.progastro.inventario.models.Entities.Marca;
import com.progastro.inventario.repositories.MarcaRepository;
import com.progastro.inventario.services.MarcaServiceBridge;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarcaServiceImpl implements MarcaServiceBridge {
    private final MarcaRepository marcaRepository;
    private final MarcaMapper marcaMapper;

    @Override
    @Transactional
    public MarcaResponseDTO registrarMarca(MarcaRequestDTO request) {
        String nombreNormalizado = request.getNombre().trim().toUpperCase();

        if (marcaRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new ValidationException("La marca '" + nombreNormalizado + "' ya existe");
        }

        Marca marca = new Marca();
        marca.setNombre(nombreNormalizado);

        Marca guardada = marcaRepository.save(marca);

        return marcaMapper.toResponse(guardada);
    }

    @Override
    public Page<MarcaResponseDTO> listarMarcas(String nombre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").descending());

        Page<Marca> marcas = marcaRepository.findByFiltro(nombre, pageable);

        return marcas.map(marcaMapper::toResponse);
    }

    @Override
    @Transactional
    public MarcaResponseDTO editarMarca(MarcaRequestDTO request) {
        String nombreNormalizado = request.getNombre().trim().toUpperCase();

        if (marcaRepository.existsByNombreIgnoreCaseAndIdMarcaNot(nombreNormalizado, request.getIdMarca())) {
            throw new ValidationException("La marca '" + nombreNormalizado + "' ya existe");
        }

        Marca marca = marcaRepository.findById(request.getIdMarca()).orElseThrow(() ->
            new ResourceNotFoundException("Marca no encontrada con el ID: " + request.getIdMarca())
        );
        marca.setNombre(nombreNormalizado);

        Marca guardada = marcaRepository.save(marca);

        return marcaMapper.toResponse(guardada);
    }
}
