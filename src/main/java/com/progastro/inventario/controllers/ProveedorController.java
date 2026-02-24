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

import com.progastro.inventario.models.DTO.PageResponse;
import com.progastro.inventario.models.DTO.ProveedorRequestDTO;
import com.progastro.inventario.models.DTO.ProveedorResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.ProveedorServiceBridge;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorServiceBridge proveedorServiceBridge;

    @PostMapping("/registrar-proveedor")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> registrarProveedor(@RequestBody @Valid ProveedorRequestDTO request) {
        ProveedorResponseDTO response = proveedorServiceBridge.registrarProveedor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Proveedor registrado correctamente", response));
    }

    @PostMapping("/editar-proveedor")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> editarProveedor(@RequestBody @Valid ProveedorRequestDTO request) {
        ProveedorResponseDTO response = proveedorServiceBridge.editarProveedor(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(true, "Proveedor editado correctamente", response));
    }

    @GetMapping("/consultar-proveedores")
    public ResponseEntity<PageResponse<ProveedorResponseDTO>> listarProveedores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String rfc,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<ProveedorResponseDTO> result = proveedorServiceBridge.listarProveedores(nombre, rfc, page, size);
        PageResponse<ProveedorResponseDTO> response = new PageResponse<>(
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