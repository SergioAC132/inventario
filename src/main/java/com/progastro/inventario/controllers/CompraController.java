package com.progastro.inventario.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.progastro.Services.CompraServiceBridge;
import com.progastro.inventario.models.DTO.CompraRequestDTO;
import com.progastro.inventario.models.DTO.CompraResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraServiceBridge compraServiceBridge;
    
    @PostMapping("/registrar-compra")
    public ResponseEntity<ApiResponse<CompraResponseDTO>> registrarCompra(@RequestBody @Valid 
                                                                        CompraRequestDTO request) {
        CompraResponseDTO response = compraServiceBridge.registrarCompra(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Compra registrada correctamente", response));
    
    }
    
}
