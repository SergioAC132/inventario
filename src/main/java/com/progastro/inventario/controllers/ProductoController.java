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

    @GetMapping("/consultar-productos")
    public ResponseEntity<PageResponse<ProductoResponseDTO>> consultarProductos(@RequestParam(required = false) String marca,
                                                                        @RequestParam(required = false) String nombre,
                                                                        @RequestParam(required = false) String codigo,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "20") int size
                                                                        ) {
        Page<ProductoResponseDTO> result = productoServiceBridge.listarProductos(marca, nombre, codigo, page, size);
        PageResponse<ProductoResponseDTO> response =
            new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
            );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/editar-producto")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> editarProducto(@RequestBody @Valid ProductoRequestDTO request) {
        ProductoResponseDTO response = productoServiceBridge.editarProducto(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(true, "Producto editado correctamente", response));
    }
}
