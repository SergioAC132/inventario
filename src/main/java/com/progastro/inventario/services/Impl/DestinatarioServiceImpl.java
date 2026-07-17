package com.progastro.inventario.services.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.mappers.DestinatarioMapper;
import com.progastro.inventario.models.DTO.DestinatarioRequestDTO;
import com.progastro.inventario.models.DTO.DestinatarioResponseDTO;
import com.progastro.inventario.models.Entities.Destinatario;
import com.progastro.inventario.repositories.DestinatarioRepository;
import com.progastro.inventario.services.DestinatarioServiceBridge;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestinatarioServiceImpl implements DestinatarioServiceBridge {

    private final DestinatarioRepository destinatarioRepository;
    private final DestinatarioMapper destinatarioMapper;

    @Override
    @Transactional
    public DestinatarioResponseDTO registrarDestinatario(DestinatarioRequestDTO request) {
        Destinatario destinatario = new Destinatario();
        destinatario.setNombre(capitalizarPalabras(request.getNombre()));

        return destinatarioMapper.toResponse(destinatarioRepository.save(destinatario));
    }

    @Override
    @Transactional
    public DestinatarioResponseDTO editarDestinatario(DestinatarioRequestDTO request) {
        Destinatario destinatario = destinatarioRepository.findById(request.getIdDestinatario()).orElseThrow(() ->
            new ResourceNotFoundException("Destinatario no encontrado con id: " + request.getIdDestinatario())
        );

        destinatario.setNombre(capitalizarPalabras(request.getNombre()));

        return destinatarioMapper.toResponse(destinatarioRepository.save(destinatario));
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
    public Page<DestinatarioResponseDTO> listarDestinatarios(String nombre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return destinatarioRepository.findByFiltros(nombre, pageable)
                .map(destinatarioMapper::toResponse);
    }
}
