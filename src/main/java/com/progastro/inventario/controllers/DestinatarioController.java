package com.progastro.inventario.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.progastro.inventario.models.DTO.DestinatarioRequestDTO;
import com.progastro.inventario.models.DTO.DestinatarioResponseDTO;
import com.progastro.inventario.models.DTO.PageResponse;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.DestinatarioServiceBridge;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/destinatarios")
@RequiredArgsConstructor
public class DestinatarioController {

    private final DestinatarioServiceBridge destinatarioServiceBridge;

    @PostMapping("/registrar-destinatario")
    public ResponseEntity<ApiResponse<DestinatarioResponseDTO>> registrarDestinatario(@RequestBody @Valid DestinatarioRequestDTO request) {
        DestinatarioResponseDTO response = destinatarioServiceBridge.registrarDestinatario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Destinatario registrado correctamente", response));
    }

    @PostMapping("/editar-destinatario")
    public ResponseEntity<ApiResponse<DestinatarioResponseDTO>> editarDestinatario(@RequestBody @Valid DestinatarioRequestDTO request) {
        DestinatarioResponseDTO response = destinatarioServiceBridge.editarDestinatario(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(true, "Destinatario editado correctamente", response));
    }

    @GetMapping("/consultar-destinatarios")
    public ResponseEntity<PageResponse<DestinatarioResponseDTO>> listarDestinatarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<DestinatarioResponseDTO> result = destinatarioServiceBridge.listarDestinatarios(nombre, page, size);
        PageResponse<DestinatarioResponseDTO> response = new PageResponse<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.isLast()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
