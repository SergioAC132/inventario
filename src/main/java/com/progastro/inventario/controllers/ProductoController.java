package com.progastro.inventario.controllers;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

    @GetMapping("/consultar-productos")
    public ResponseEntity<Page<ProductoResponseDTO>> consultarProductos(@RequestParam(required = false) String marca,
                                                                        @RequestParam(required = false) String nombre,
                                                                        @RequestParam(required = false) String codigo,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "20") int size
                                                                        ) {
        Page<ProductoResponseDTO> result = productoServiceBridge.listarProductos(marca, nombre, codigo, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }    
}
