package com.progastro.inventario.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progastro.inventario.models.DTO.ProductoRequestDTO;
import com.progastro.inventario.models.DTO.ProductoResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.ProductoServiceBridge;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    
    private final ProductoServiceBridge productoServiceBridge;

    @PostMapping("/registrar-producto")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> registrarProducto(@RequestBody @Valid
                                                                            ProductoRequestDTO request) {
        ProductoResponseDTO response = productoServiceBridge.registrarProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Producto registrado correctamente", response));
    }
}
