package com.progastro.inventario.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/inventarios")
@RequiredArgsConstructor
public class InventarioController {
    
    private final InventarioServiceBridge inventarioServiceBridge;

    @GetMapping("/{idProducto}/inventarios")
    public ResponseEntity<ApiResponse<InventarioResponseDTO> listarInventarios(@RequestParam String param) {
        return new String();
    }
    
}
