package com.progastro.inventario.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progastro.inventario.models.Response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/salidas")
@RequiredArgsConstructor
public class SalidaController {
    
    private final SalidaServiceBridge salidaServiceBridge;

    @PostMapping("/registrar-salida")
    public ResponseEntity<ApiResponse<SalidaResponseDTO>> registrarSalida(@RequestBody @Valid
                                                                            SalidaRequestDTO request) {
        SalidaResponseDTO response = salidaServiceBridge.registrarSalida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Salida registrada correctamente", response));
    }
    
}
